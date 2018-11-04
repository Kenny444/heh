package com.heh.upload.servlet;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UploadServlet handles file upload and retrieval. Files may have a prefix applied for uniqueness, 
 * Image files can be scaled and thumbnails generated, and videos can have thumbnails generated.
 * 
 * Optional override web.xml parameters:
 * root - the file location where uploads are stored
 * requireAuth - true to require only an authenticated user to do uploads, false if anyone can upload (DANGEROUS!)
 * 
 * Upload a file:
 * The request parameter name should be "file". Only one file can be included per request.
 * 
 * JSON response to upload:
 * {
 *	status: true,
 *	url: "URL to file"
 * }
 * 
 * URL Options (e.g. uploads/noprefix)
 * noprefix - do not add a unique prefix to the uploaded file name.
 * filelist - get the list of files in the upload location
 *		JSON response: {
						"status": true,
						"files": [
							"/test/uploads/IMG_0077.jpg",
							"/test/uploads/IMG_6805.jpg",
							"/test/uploads/jakson.jpg"
						]
						}
 * scale/px height - e.g. scale/1080 scale the uploaded image to a maximum height. If the image height is smaller
 *						than this, do nothing.
 * thumbscale/px height - like scale/px height, but specific that a thumbnail image should be generated
 *						for the image. Thumbnails are named the same as the file but start with a "th-"
 * 
 * 
 * 
 * @author kents
 */
@WebServlet(name = "UploadServlet", urlPatterns = "/uploads/*", initParams = {
	@WebInitParam(name = "root", value = "/Users/kennethharris/downloads"),
@WebInitParam(name = "requireAuth", value = "false")})
public class UploadServlet extends HttpServlet {

	private static String root = "";
	private static boolean requireAuth = true;
	private static Logger logger = LoggerFactory.getLogger(UploadServlet.class);

	public static String getRoot() {
		return root;
	}

	private static FilenameFilter photoFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			String lc = name.toLowerCase();
			return lc.endsWith(".jpg") || lc.endsWith(".jpeg") || lc.endsWith(".gif") || lc.endsWith(".png");
		}

	};

	/**
	 * lastUpload tracks the time of the last uploaded file, from anyone. This resource is protected with synchronized
	 * blocks. The use of lastUpload is to guarantee the uniqueness of filenames being uploaded, since the current
	 * timestamp will be applied to the file. If two file uploads complete within the same millisec, one will wait for 1
	 * millisec to enforce this uniqueness.
	 */
	private long lastUpload = 0;

	/**
	 * Called by Tomat on the first access to this servlet to initialize any data, typically from init parameters
	 * specified either in web.xml or as annotations on the servlet.
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		root = config.getInitParameter("root");
		File f = new File(root);
		f.mkdirs();
		requireAuth = Boolean.parseBoolean(config.getInitParameter("requireAuth"));
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Pathinfo components are: action
	 *
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "max-age=86400");
		String[] parms = null;
		if (request.getPathInfo() != null) {
			parms = request.getPathInfo().substring(1).split("/");
		}
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
//				String userName = null;
				Principal principal = request.getUserPrincipal();
				if (requireAuth && principal == null) {
					sendErrorJson(response, "Not signed in");
					return;
				}
//				userName = getUser(request).toLowerCase();
				//upload request
				try {
					DiskFileItemFactory factory = new DiskFileItemFactory();
					File repository = (File) request.getServletContext().getAttribute("javax.servlet.context.tempdir");
					factory.setRepository(repository);
					ServletFileUpload upload = new ServletFileUpload(factory);
					// Parse the request
					List<FileItem> items = upload.parseRequest(request);
					handleUpload(request, response, items, parms);
				} catch (FileUploadException e) {
					logger.error("", e);
				}
				return;
			} else if (contains(parms, "filelist")) {
				handleFileList(request, response);
			} else {
				//retrieval request
				handleSend(request, response);
			}
		} catch (Throwable t) {
			logger.error("", t);
			sendErrorJson(response, "Could not process request");
		}
		/*
		uploads/images/netflix.jpg
		 */
	}

	public static void sendErrorJson(HttpServletResponse response, String message) throws IOException {
		JSONObject errorMsg = new JSONObject();

		errorMsg.put("status", false);
		errorMsg.put("message", message);
		response.getWriter().write(errorMsg.toString());
	}

	public static void sendOkJson(HttpServletResponse response) throws IOException {
		JSONObject okMsg = new JSONObject();

		okMsg.put("status", true);
		response.getWriter().write(okMsg.toString());
	}

	private void handleFileList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		File f = new File(root);
		if (!f.exists()) {
			f.mkdirs();
		}
		String[] files = f.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isFile();
			}
		});
		Arrays.sort(files, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}

		});
		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		JSONObject resp = new JSONObject();
		resp.put("status", true);
		JSONArray list = new JSONArray();
		resp.put("files", list);
		for (String file : files) {
			list.put(request.getContextPath()+"/uploads/"+file);
		}
		writer.write(resp.toString());
	}

	private boolean contains(String[] parms, String name) {
		if (parms != null) {
			for (String parm : parms) {
				if (parm.equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

	private int containsGetIndex(String[] parms, String name) {
		if (parms != null) {
			for (int i = 0; i < parms.length; i++) {
				String parm = parms[i];
				if (parm.equals(name)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Process the file upload(s), save them to disk, and generate a JSON response indicating the URI to the resource
	 * that can be used in the web page to access the uploaded file.
	 *
	 * @param request
	 * @param response
	 * @param dest
	 */
	protected void handleUpload(HttpServletRequest request, HttpServletResponse response, List<FileItem> items, String[] parms) throws Throwable {
		response.setContentType("application/json");
		File f = new File(root);
		if (!f.exists()) {
			f.mkdirs();
		}
		JSONObject resp = new JSONObject();
		resp.put("status", true);
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			String pathName = "";
			Map<String, String> parmsMap = new HashMap<>();
			for (FileItem item : items) {
				if (item.isFormField()) {
					String name = item.getFieldName();
					parmsMap.put(name, item.getString());
				}
			}
			for (FileItem item : items) {
				if (!item.isFormField()) {
					String name = item.getName();
					int idx = name.lastIndexOf(File.separatorChar);
					if (idx != -1) {
						name = name.substring(idx + 1);
					}
					long now;

					if (!contains(parms, "noprefix")) {
						synchronized (getClass()) {
							now = System.currentTimeMillis();
							//if another request thread has already been processed in this same millisec timestamp,
							//wait for 1 millisec so that the timestamp put into the name of the uploaded file will
							//be unique.
							if (now == lastUpload) {
								Thread.sleep(1);
							}
							now = System.currentTimeMillis();
							//update timestamp of last processed upload
							lastUpload = now;
						}
						//prefix the filename with the timestamp for uniqueness
						name = now + name;
					}
					if (!(pathName.length() == name.length() + 2 && pathName.indexOf(name) >= 0)) {
						name = pathName + name;
					}

					InputStream fstream = null;
					try {
						//open the file stream to the uploaded data and then save it to disk
						fstream = item.getInputStream();
						String nlc = name.toLowerCase();
						if (nlc.endsWith(".jpeg") || nlc.endsWith(".jpg")) {
							int dot = nlc.lastIndexOf(".");
							name = name.substring(0, dot+1) + "jpg";
							nlc = name.toLowerCase();
						}
						File savedFile = saveFile(f, name, fstream);
						if (nlc.endsWith(".jpg") || nlc.endsWith(".gif") || nlc.endsWith(".png")) {
							int scaleIdx = containsGetIndex(parms, "scale");
							if (scaleIdx >= 0) {
								scaleImage(savedFile, savedFile, Integer.parseInt(parms[scaleIdx + 1]), nlc.substring(nlc.length() - 3));
							}
							processMedia(savedFile.getCanonicalPath(), parms);
						}
						//add the URI to access this file to the JSON response
						resp.put("url",request.getContextPath() + "/uploads/" + name);
					} finally {
						if (fstream != null) {
							fstream.close();
						}
					}
				}
			}
		} catch (Throwable t) {
			logger.error("Could not process upload", t);
			JSONObject json = new JSONObject();
			json.put("error", "Could not process upload, please try again later. Sorry for the inconvenience!");
			writer.write(json.toString());
			return;
		}
		writer.print(resp.toString());
	}

	public void processMedia(String name, String[] parms) throws Throwable {
		String shortName = name;
		String pathName = "";
		int slash = name.lastIndexOf(File.separatorChar);
		if (slash > 0) {
			shortName = name.substring(slash + 1);
			pathName = name.substring(0, slash + 1);
			logger.debug("processMedia {}:{}", shortName, pathName);
		}
		File savedFile = new File(pathName + "th-" + shortName);
		File srcFile = new File(name);
		if (name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".png")) {
			int scaleIdx = containsGetIndex(parms, "thumbscale");
			if (scaleIdx >= 0) {
				scaleImage(srcFile, savedFile, Integer.parseInt(parms[scaleIdx + 1]), name.substring(name.length() - 3));
			}
		} else if (name.endsWith(".mp4") || name.endsWith(".mov")) {
			int scaleIdx = containsGetIndex(parms, "thumbscale");
			if (scaleIdx >= 0) {
				String outName = savedFile.getCanonicalPath();
				outName = outName.substring(0, outName.length() - 3) + "jpg";
				File outFile = new File(outName);
				scaleVideo(srcFile, outFile, Integer.parseInt(parms[scaleIdx + 1]), "jpg");
			}
		}
	}

	/**
	 * Save the uploaded file using it's stream to disk
	 *
	 * @param path
	 * @param name
	 * @param stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private File saveFile(File path, String name, InputStream stream) throws FileNotFoundException, IOException {
		File f = new File(path, name);
		String folder = f.getCanonicalPath();
		folder = folder.substring(0, folder.lastIndexOf(File.separator));
		File folderFile = new File(folder);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			pipeStream(stream, fos);
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
		return f;
	}

	/**
	 * Retrieve the requested uploaded file and stream it back to the browser.
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	protected void handleSend(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = request.getPathInfo();
		// images/netflix.jpg
		logger.debug("handleSend {}", path);
		if (path != null) {
			File file = new File(root);
			file = new File(file, path);
			sendFile(request, response, file);
		}
	}

	private void sendFile(HttpServletRequest request, HttpServletResponse response, File file) throws FileNotFoundException, IOException {
		ServletOutputStream output = response.getOutputStream();
		String contentType = getContentType(file.getName());
		logger.debug("content type " + contentType);
		if (contentType != null) {
			response.setContentType(contentType);
			if (file.exists()) {
				FileInputStream input = null;
				BufferedInputStream data = null;
				try {
					input = new FileInputStream(file);
					data = new BufferedInputStream(input);
					pipeStream(input, output);
				} finally {
					if (data != null) {
						try {
							data.close();
						} catch (Throwable t) {
						}
					}
					if (input != null) {
						try {
							input.close();
						} catch (Throwable t) {
						}
					}
				}
			}
		}

	}

	/**
	 * Utility method the take all the data from an input stream and write it to the output stream. This method could be
	 * a static method in a utility class since it accesses nothing from this class.
	 *
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	private void pipeStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[8192];
		while (true) {
			int len = input.read(buffer);
			if (len == -1) {
				break;
			}
			output.write(buffer, 0, len);
		}
	}

	/**
	 * Set the response stream's content type based on the extension of the uploaded file.
	 *
	 * @param name
	 * @return
	 */
	private String getContentType(String name) {
		String ext = "";
		int extIndex = name.lastIndexOf('.');
		if (extIndex > -1) {
			ext = name.substring(extIndex + 1).toLowerCase();
		}
		if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif") || ext.equals("png")) {
			return ("image/" + ext);
		} else if (ext.equals("pdf")) {
			return ("application/pdf");
		} else if (ext.equals("doc")) {
			return ("application/msword");
		} else if (ext.equals("xls")) {
			return ("application/vnd.ms-excel");
		} else if (ext.equals("txt")) {
			return ("text/plain; charset=UTF-8");
		} else if (ext.equals("html")) {
			return ("text/html");
		} else if (ext.equals("css")) {
			return ("text/css");
		} else if (ext.equals("js")) {
			return ("application/javascript");
		} else if (ext.equals("mov")) {
			return ("video/quicktime");
		} else if (ext.equals("mp4")) {
			return ("video/mp4");
		} else if (ext.equals("zip")) {
			return ("application/zip");
		} else if (ext.equals("ttf")) {
			return ("font/ttf");
		} else if (ext.equals("svg")) {
			return ("image/svg+xml");
		} else if (ext.equals("eot")) {
			return ("application/vnd.ms-fontobject");
		} else if (ext.equals("woff")) {
			return ("font/woff");
		} else if (ext.equals("woff2")) {
			return ("font/woff2");
		} else if (ext.equals("mp3")) {
			return ("audio/mp3");
		}
		return null;
	}

	public void scaleImage(File inputFile, File outputFile, int photoHeight, String ext) throws Throwable {
		logger.debug("scaleImage {} {} {}", photoHeight, inputFile.getCanonicalPath(), outputFile.getCanonicalPath());
		BufferedImage src = ImageIO.read(inputFile);
		while (src.getHeight() == 0 || src.getWidth() == 0) {
			try {
				logger.debug("image sleeping");
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}
		if (src.getHeight() <= photoHeight) {
			return;
		}
		float scale = (float) photoHeight / (float) src.getHeight();
		logger.debug("size {} {}", (int) (scale * src.getWidth()), photoHeight);
		ResampleOp resampleOp = new ResampleOp((int) (scale * src.getWidth()), photoHeight);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		BufferedImage rescaled = resampleOp.filter(src, null);
		ImageIO.write(rescaled, ext, outputFile);
	}

	public void scaleVideo(File inputFile, File outputFile, int photoHeight, String ext) throws Throwable {
		logger.debug("scaleVideo {} {} {}", photoHeight, inputFile.getCanonicalPath(), outputFile.getCanonicalPath());

		int frameNumber = 1;
		Picture picture = FrameGrab.getFrameFromFile(inputFile, frameNumber);

		BufferedImage src = AWTUtil.toBufferedImage(picture);
		while (src.getHeight() == 0 || src.getWidth() == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}
		if (src.getHeight() <= photoHeight) {
			return;
		}
		float scale = (float) photoHeight / (float) src.getHeight();
		ResampleOp resampleOp = new ResampleOp((int) (scale * src.getWidth()), photoHeight);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		BufferedImage rescaled = resampleOp.filter(src, null);
		ImageIO.write(rescaled, ext, outputFile);
	}
}
