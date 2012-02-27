package common;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class UploadUtils {
	private final static Logger logger = Logger
			.getLogger(UploadUtils.class);
	private final static String uploadPath = "file";

	/**
	 * �ж��Ƿ����ϴ��ļ�
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isMultipart(HttpServletRequest request) {
		return FileUpload
				.isMultipartContent(new ServletRequestContext(request));
	}

	/**
	 * �ϴ��ļ�
	 * 
	 * @param request
	 * @return �ļ����� ����ö��ŷָ���
	 * @throws FileUploadException
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public static String upload(HttpServletRequest request) {
		if (!isMultipart(request)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		try {

			String basePath = request.getRealPath("/") + uploadPath;

			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setRepository(new File(basePath));
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(2000000);
			List<FileItem> items = upload.parseRequest(request);
			for (FileItem fileItem : items) {
				if (fileItem.isFormField()) {
					continue;
				}

				if (fileItem.getName() != null && fileItem.getSize() != 0) {
					String fileName = getFileName(basePath,
							getExtention(fileItem));
					File newFile = new File(basePath + "/" + fileName);
					fileItem.write(newFile);
					sb.append(fileName).append(",");

				} else {
					logger.debug("�ļ�û��ѡ�� �� �ļ�����Ϊ��");
				}
			}
		} catch (Exception e) {
			logger.error("�ϴ��ļ�����", e);
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
			logger.debug("���ϴ��ļ�:" + sb.toString());
		}
		return sb.toString();
	}

	/**
	 * ��ȡ�ļ���չ��
	 * 
	 * @param fileItem
	 * @return .jpg
	 */
	private static String getExtention(FileItem fileItem) {
		if (fileItem.getName() != null) {
			return fileItem.getName()
					.substring(fileItem.getName().indexOf("."));
		}
		return null;
	}

	/**
	 * ��ȡ�ļ��������ݵ�ǰʱ�䣩
	 * 
	 * @param basePath
	 * @param ext
	 * @return
	 */
	private static String getFileName(String basePath, String ext) {
		String s = String.valueOf(Calendar.getInstance().getTimeInMillis());
		String name = s + (StringUtils.isNotBlank(ext) ? ext : "");
		File newFile = new File(basePath + name);
		if (newFile.exists()) {
			return getFileName(basePath, ext);
		}
		return name;
	}

	public static void main(String[] args) {
		System.out.println(getFileName("c:/tmp/", ".jpg"));
	}
}
