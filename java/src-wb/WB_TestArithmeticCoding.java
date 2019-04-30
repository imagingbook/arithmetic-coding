import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

public class WB_TestArithmeticCoding {

	public static void main(String[] args) {
		System.out.println("starting");
//		byte[] original = {3, 2, 9, 1, 5, 3, 2, 8, 9, 1, 0, -1};
//		byte[] original = {0,3,3,3,3,3,3,3,3,3,3};
		byte[] original = makeRandom(1000000);
		
		//System.out.println("original = " + Arrays.toString(original));
		
		System.out.println("compressing " + original.length);
		byte[] compressed = compress(original);
		//System.out.println("compressed = " + Arrays.toString(compressed));
		
		System.out.println("decompressing " + compressed.length);
		byte[] restored = decompress(compressed);
		//System.out.println("restored = " + Arrays.toString(restored));
		
		System.out.println("restored " + restored.length);
	}
	
	private static byte[] makeRandom(int n) {
		byte[] a = new byte[n];
		Random rd = new Random();
		for (int i = 0; i < n; i++) {
			a[i] = (byte) rd.nextInt(2);
		}
		return a;
	}

	static byte[] compress(byte[] b) { //  throws IOException 
		FrequencyTable freqs = new SimpleFrequencyTable(new int[257]);
		for (byte x : b)
			freqs.increment(x & 0xFF);
		freqs.increment(256);  // EOF symbol gets a frequency of 1
		
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (BitOutputStream bitOut = new BitOutputStream(out)) {
			ArithmeticCompress.writeFrequencies(bitOut, freqs);
			ArithmeticCompress.compress(freqs, in, bitOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
	
	static byte[] decompress(byte[] b) {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BitInputStream bitIn = new BitInputStream(in);
		
		FrequencyTable freqs;
		try {
			freqs = ArithmeticDecompress.readFrequencies(bitIn);
			ArithmeticDecompress.decompress(freqs, bitIn, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

}
