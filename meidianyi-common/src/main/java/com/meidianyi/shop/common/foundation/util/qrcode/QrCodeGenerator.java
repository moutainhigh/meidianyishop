package com.meidianyi.shop.common.foundation.util.qrcode;

import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
/**
* @author 黄壮壮
* @Date: 2019年11月27日
* @Description: 二维码生成工具
*/
public class QrCodeGenerator {
	private static final String QR_CODE_IMAGE_PATH = "./MyQRCode.jpg";
	private static final String BAR_CODE_IMAGE_PATH = "./MyBarCode.jpg";
	/**
	 * 传入加密的text信息以及QRCode的长宽，生成二维码
	 * @param text  
	 * @param width
	 * @param height
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	public static byte[] generateQrCodeImg(String text, int width, int height) throws WriterException, IOException {
		QRCodeWriter qrWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		
		ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "JPG", jpgOutputStream);
		
		byte[] jpgData = jpgOutputStream.toByteArray();
		return jpgData;
	}
	
	/**
	 * 传入加密的text信息以及QRCode的长宽，生成条形码
	 * @param text
	 * @param width
	 * @param height
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	public static byte[] generateBarCodeImg(String text,int width,int height) throws WriterException, IOException {
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.CODE_128, width, height,null);
		
		
		ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "JPG", jpgOutputStream);
		
		byte[] jpgData = jpgOutputStream.toByteArray();
		return jpgData;
	}
	
	private static void generateQrCodeImg(String text, int width, int height, String filePath) throws WriterException, IOException {
		QRCodeWriter qrWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		
		Path path = FileSystems.getDefault().getPath(filePath);
		MatrixToImageWriter.writeToPath(bitMatrix, "JPG", path);
	}
	
	private static void generateBarCodeImg(String text,int width,int height,String filePath) throws WriterException, IOException {
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.CODE_128, width, height,null);
		Path path = FileSystems.getDefault().getPath(filePath);
		MatrixToImageWriter.writeToPath(bitMatrix, "JPG", path);
	}
		
	
	public static void main(String[] args) {
        try {
        	generateQrCodeImg("This is my first QR Code", 350, 350, QR_CODE_IMAGE_PATH);
        	generateBarCodeImg("2455471008284385",100,80,BAR_CODE_IMAGE_PATH);
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }
    }
	
}
