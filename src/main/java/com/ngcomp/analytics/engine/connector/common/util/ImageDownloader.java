package com.ngcomp.analytics.engine.connector.common.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.ngcomp.analytics.engine.connector.common.CommonCredentials;

import java.io.*;
import java.net.URL;
import java.util.Date;

/**
 * Common utility class to handle image download.
 * @author dprasad
 * @since 0.1
 */
public class ImageDownloader {
	
	/**
	 * Method to downloads and save the images given an image URL.
	 * 
	 * @param imageUrl
	 */
	public static String downloadAndSaveImage(String imageUrl) {
		
		File tempImageFile = downloadAndSaveImageToTemp(imageUrl);
		String keyName = new Date().getTime() + imageUrl.substring(imageUrl.lastIndexOf("/"));
		
		AWSCredentials myCredentials = new BasicAWSCredentials(
				CommonCredentials.AMAZON_ACCESS_KEY_ID,
				CommonCredentials.AMAZON_ACCESS_KEY);

		AmazonS3Client s3Client = new AmazonS3Client(myCredentials);
		String finalURL = addFileNew(s3Client, keyName, tempImageFile);
		return finalURL;
		
	}
	
	private static String addFileNew(AmazonS3Client s3Client, String keyName,
                                     File file) {

		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

		String bucketName = CommonCredentials.AMAZON_S3_BUCKET;

		//s3Client.putObject(new PutObjectRequest(bucketName,	keyName, file).withAccessControlList(acl));
		
		System.out.println(file.getAbsolutePath());
		file.deleteOnExit();
		
		String finalURL = CommonCredentials.AMAZON_S3_REGION_STRING + bucketName+ "/"+ keyName;
		return finalURL;
	}
	
	
	private static File downloadAndSaveImageToTemp(String imageUrl) {
		URL url = null;
		String fileName = System.getProperty("java.io.tmpdir") + imageUrl.substring(imageUrl.lastIndexOf("/")+1);
		String outputLocation = fileName;
		try {

			url = new URL(imageUrl);
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();

			byte[] response = out.toByteArray();

			FileOutputStream fos = new FileOutputStream(outputLocation);
			
			fos.write(response);
			
			fos.close();

		} catch (Exception e) {

		}
		return new File(outputLocation);
	}
	
	
	public static void downloadAndSaveImageP(String imageUrl, String outputLocation) {
		URL url = null;
		try {

			url = new URL(imageUrl);
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();

			byte[] response = out.toByteArray();

			FileOutputStream fos = new FileOutputStream(outputLocation);
			
			fos.write(response);
			
			fos.close();

		} catch (Exception e) {

		}

	}
	
}
