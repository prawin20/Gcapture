package capture;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import util.GifSequenceWriter;

public class Main {

	public static void main(String[] args) throws AWTException, InterruptedException, IOException {
		Main main = new Main();

		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_TAB);
		robot.delay(100);
		robot.keyRelease(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_ALT);
		final Thread thisThread = Thread.currentThread();
		int i = 0;
		System.out.println("Capture started");
		if (args.length == 3) {
			final int timeToRun = Integer.parseInt(args[2]);

			new Thread(() -> {
				try {
					Thread.sleep(timeToRun);
					thisThread.interrupt();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}).start();

			while (!Thread.interrupted()) {
				main.screenCapture(i, args[0]);
				i++;
			}
		}

		else {
			while (System.in.available() == 0) {
				main.screenCapture(i, args[0]);
				i++;
			}
			i = i - 20;
		}
		System.out.println("Capture ended");
		main.toGifSequence(i, args[0], args[1]);

	}

	public void screenCapture(int i, String path) throws AWTException, InterruptedException {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle rectangle = new Rectangle(dimension);
		Robot robot = new Robot();
		BufferedImage screen = robot.createScreenCapture(rectangle);
		String fileName = "screenshot";
		try {
			ImageIO.write(screen, "jpg", new File(path + File.separator + fileName + i + ".jpg"));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void toGifSequence(int num, String path, String outputFileName) throws IOException {
		System.out.println("GIF convertion starts");
		String fileName = "screenshot";
		BufferedImage firstImage = ImageIO.read(new File(path + File.separator + fileName + 0 + ".jpg"));

		// create a new BufferedOutputStream with the last argument
		ImageOutputStream output = new FileImageOutputStream(new File(path + File.separator + outputFileName + ".gif"));

		// create a gif sequence with the type of the first image, 1 second
		// between frames, which loops continuously
		GifSequenceWriter writer = new GifSequenceWriter(output, TYPE_INT_RGB, 1, true);

		// write out the first image to our sequence...
		writer.writeToSequence(firstImage);
		int i = 0;
		for (i = 0; i < num; i++) {
			BufferedImage nextImage = ImageIO.read(new File(path + File.separator + fileName + i + ".jpg"));
			writer.writeToSequence(nextImage);
			new File(path + File.separator + fileName + i + ".jpg").delete();
		}
		int j = i + 20;
		for (; i < j; i++) {
			new File(path + File.separator + fileName + i + ".jpg").delete();
		}
		writer.close();
		output.close();
		System.out.println("GIF convertion ends");
	}

}
