package ma.mondial2030.util;

import com.google.zxing.LuminanceSource;

import java.awt.image.BufferedImage;

/**
 * Source de luminance pour la lecture de QR Code depuis BufferedImage
 */
public class BufferedImageLuminanceSource extends LuminanceSource {
    private final BufferedImage image;
    private final int[] data;

    public BufferedImageLuminanceSource(BufferedImage image) {
        super(image.getWidth(), image.getHeight());
        this.image = image;
        this.data = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), data, 0, image.getWidth());
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        int width = getWidth();
        if (row == null || row.length < width) {
            row = new byte[width];
        }
        int offset = y * width;
        for (int x = 0; x < width; x++) {
            int pixel = data[offset + x];
            int luminance = (306 * ((pixel >> 16) & 0xFF) +
                            601 * ((pixel >> 8) & 0xFF) +
                            117 * (pixel & 0xFF)) >> 10;
            row[x] = (byte) luminance;
        }
        return row;
    }

    @Override
    public byte[] getMatrix() {
        int width = getWidth();
        int height = getHeight();
        byte[] matrix = new byte[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = data[offset + x];
                int luminance = (306 * ((pixel >> 16) & 0xFF) +
                                601 * ((pixel >> 8) & 0xFF) +
                                117 * (pixel & 0xFF)) >> 10;
                matrix[offset + x] = (byte) luminance;
            }
        }
        return matrix;
    }

    @Override
    public boolean isCropSupported() {
        return true;
    }

    @Override
    public LuminanceSource crop(int left, int top, int width, int height) {
        return super.crop(left, top, width, height);
    }

    @Override
    public boolean isRotateSupported() {
        return false;
    }
}
