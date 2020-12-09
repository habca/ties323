package thread;

import java.net.*;
import java.util.*;

import static org.junit.Assert.*;

import org.junit.*;

/**
 * UDP packet that implements CRC8 checksum
 * @author Harri Linna
 * @version 5.12.2020
 */
public class DatagramPacketCRC8 extends ADatagramPacket {

	private static final int[] mask = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80 };
	
	public DatagramPacketCRC8(DatagramPacket packet) {
		super(packet);
	}
	
	// PUBLIC METHODS
	
	public final boolean isCorrupted() {
		return checksum(getData()) != 0;
	}
	
	@Override
	public String toString() {
		return new String(getData(), 0, getLength() - 1); // remove CRC8
	}
	
	public DatagramPacket removeCRC8() {
		byte[] arr = Arrays.copyOfRange(getData(), 0, getLength()-1);
		return new DatagramPacket(arr, arr.length, getAddress(), getPort());
	}
	
	// PRIVATE METHODS
	
	public static DatagramPacketCRC8 convertToCRC8(DatagramPacket packet) {
		byte[] data = encode(packet.getData()); // add CRC8
		return new DatagramPacketCRC8(new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort()));
	}
	
	private static byte[] encode(byte[] arr) {
		int size = arr.length;
		byte[] data = new byte[size + 1];

		data[size] = checksum(arr);
		for (int i = 0; i < size; i++) {
			data[i] = arr[i];
		}

		return data;
	}
	
	private static byte checksum(byte[] data) {
		byte register = 0;

		for (int i = 0; i < data.length; i++) {
			register = generateByte(register, data[i]);
		}

		return generateByte(register, (byte) 0);
	}
	
	private static byte generateByte(byte init, byte data) {
		byte register = init;
		int last = mask.length - 1;

		for (int i = last; i >= 0; i--) {
			byte previous = register;
			register = (byte) (register << 1);

			byte left = (byte) ((previous & mask[last]) >>> last);

			byte right = (byte) ((previous & mask[1]) >>> 1);
			byte bit = (byte) ((left ^ right) << 2);
			register = (byte) (register & ~mask[2]);
			register = (byte) (register | bit);

			right = (byte) ((previous & mask[0]) >>> 0);
			bit = (byte) ((left ^ right) << 1);
			register = (byte) (register & ~mask[1]);
			register = (byte) (register | bit);

			right = (byte) ((data & mask[i]) >>> i);
			bit = (byte) ((left ^ right) << 0);
			register = (byte) (register & ~mask[0]);
			register = (byte) (register | bit);
		}

		return register;
	}
	
	// JUNIT TESTS
	
	public static class TestAPacketError {
		
		// tarvitaan vain testeihin siksi private
		private static boolean corrupt(byte[] data) {
			return checksum(data) != 0;
		}
		
		@Test
		public void testChecksum() {
			byte input = 97;
			byte[] test = { input };
			byte output = checksum(test);

			assertEquals(97, input); // 01100001 bin, 97 dec
			assertEquals(32, output); // 00100000 bin, 32 dec
		}
		
		@Test
		public void testIsCorrupted() {
			byte input = 97;
			
			byte[] test = { input };
			byte[] test2 = { input, checksum(test) };
			byte[] test3 = { input, 33 }; // 00100001 bin, dec 33
			
			assertFalse(corrupt(test2));
			assertTrue(corrupt(test3));
			
			byte[] test4 = { 5, input };
			byte[] test5 = { 5, input, checksum(test4) };
			
			assertFalse(corrupt(test5));
		}
		
		@Test
		public void testRemoveCRC8() {
			byte[] arr = "testdata".getBytes();
			
			DatagramPacket packet = new DatagramPacket(arr, arr.length, null, 0);
			DatagramPacketCRC8 error = DatagramPacketCRC8.convertToCRC8(packet);
			DatagramPacket expect = error.removeCRC8();
			
			assertFalse(error.isCorrupted());
			
			assertEquals(packet.getLength() + 1, error.getLength());
			assertFalse(Arrays.equals(packet.getData(), error.getData()));
			
			assertTrue(Arrays.equals(packet.getData(), expect.getData()));
			assertEquals(packet.getLength(), expect.getLength());
		}
	}
	
}
