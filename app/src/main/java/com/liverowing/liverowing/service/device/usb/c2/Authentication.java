package com.liverowing.liverowing.service.device.usb.c2;

import java.nio.ByteOrder;

public class Authentication {

	private final int APCOMM_AUTHEN_KEY_LEN = 4;
	private final int APCOMM_AUTHEN_PW_LEN = 2;
	private final int APCOMM_AUTHEN_SOURCE_LEN = 2;
	private final int APCOMM_AUTHEN_KEY1_1 = 0x01071984;
	private final int APCOMM_AUTHEN_KEY1_2 = 0x12221959;
	private final int APCOMM_AUTHEN_KEY1_3 = 0x12301958;
	private final int APCOMM_AUTHEN_KEY1_4 = 0x03191960;
	private final int[] apcomm_authenkey1 = { APCOMM_AUTHEN_KEY1_1, APCOMM_AUTHEN_KEY1_2, APCOMM_AUTHEN_KEY1_3, APCOMM_AUTHEN_KEY1_4 };

	/* Definitions for local constants */
	private final int TKCIPHER_DELTA = 0x9E3779B9;
	private final int TKCIPHER_SUM = (TKCIPHER_DELTA << 5);
	private final int TKCIPHER_NUMBITS = 32;

	
	// The cipher source array is loaded with two copies of the 32-bit PM serial
	// #.
	// The cipher block length is 8 bytes (64 bits). The
	// resulting authentication password is returned as two 32-bit values.

	public int[] getPassword(int serialNumber) {
		
		int[] temp_ciphersource = new int[APCOMM_AUTHEN_SOURCE_LEN];
		int temp_cipherblockbytelen;
		int[] apcomm_authenpassword = new int[APCOMM_AUTHEN_PW_LEN];

		// Swap function performs Big Endian alignment of data
		temp_ciphersource[0] = SWAP_UINT32_M(serialNumber);
		temp_ciphersource[1] = temp_ciphersource[0];

		temp_cipherblockbytelen = APCOMM_AUTHEN_SOURCE_LEN * Integer.SIZE/8;

		apcomm_authenpassword = tkcipher_encode(temp_ciphersource, apcomm_authenkey1, temp_cipherblockbytelen);
		return apcomm_authenpassword;
	}

		
	
	
	/* Helper macro for little endian systems */
	private int SWAP_UINT32_M(int a) {
		return (((((a) & 0xff) << 24) | ((a) >>> 24) & 0xff) | (((a) & 0xff0000) >>> 8 | (((a) & 0xff00) << 8)));
	}

	private int TKCIPHER_PAD_LONG_M(int val, int numbytes) {
		return ((val) & (0xffffffff >>> (((numbytes) & 3) << 3)));
	}

	private int TKCIPHER_FIX_ORDER_M(int val) {
		return SWAP_UINT32_M(val);
	}

	/****************************************************************************
	 * tkcipher_encode
	 * 
	 * About: Block encipher with padding
	 * 
	 * Inputs: UINT32_T *srcblock Location of data to be encoded 
	 *         UINT32_T *key Location of 128-bit key for encoding 
	 *         UINT32_T *numbytes Number of bytes to encode
	 * 
	 * Outputs: UINT32_T *destblock Location to store encoded data 
	 *          UINT32_T *numbytes Number of bytes encoded, including pad bytes
	 * 
	 * Returns: ERRCODE_T ecode TKCIPHER_OK, successful
	 ****************************************************************************/

	public int[] tkcipher_encode(int srcblock[], int key[], int numbytes) {
	   int destblock[] = new int[2];
	   int padlength;
	   int numblocks;
	   int[] tempsrc = new int[2];
	   int srcBlockPointer = 0;
	   int destBlockPointer = 0;
	   
	int[] local_key = new int[4];		
	 if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
		   /* Copy key to local variable to ensure proper alignment on 32-bit boundaries in memory */
		   local_key = key;
	}
	

	   /* Compute the number of full 8 byte blocks and pad length from number of bytes */
	   numblocks = numbytes >>> 3;
	   padlength =  ((8 - (numbytes & 0x7)) & 0x7);
	   numbytes += padlength;

	   /* Encode the desired number of bytes, padding as necessary */
	   while (numblocks-- > 0) {
	      /* Save this data for conversion */
	      tempsrc[0] = srcblock[srcBlockPointer]; srcBlockPointer++;
	      tempsrc[1] = srcblock[srcBlockPointer]; srcBlockPointer++;

	      /* Encrypt the source data */
	 	 if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) destblock = tkcipher_encipher(tempsrc, key);
	 	 else destblock = tkcipher_encipher(tempsrc, local_key);
	 	 
	      /* Increment to the next 8 bytes */
	      destBlockPointer += 2;
	   }

	   /* Perform padding on last block if necessary */
	   if (padlength != 0) {
	      if (padlength <= 4) {
	         tempsrc[0] = srcblock[srcBlockPointer]; srcBlockPointer++;
	         tempsrc[1] = TKCIPHER_PAD_LONG_M(srcblock[srcBlockPointer], padlength);
	      }
	      else {
	         tempsrc[0] = TKCIPHER_PAD_LONG_M(srcblock[srcBlockPointer], padlength);
	         tempsrc[1] = 0;
	      }
	      
	      /* Encrypt the last partial block */
	 	 if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) destblock = tkcipher_encipher(tempsrc, key);
	 	 else destblock = tkcipher_encipher(tempsrc, local_key);
	   }

	   return destblock;
	}

	/****************************************************************************
	 * tkcipher_encipher
	 * 
	 * About: Encipher a group of 8 bytes
	 * 
	 * Inputs: 	UINT32_T *s   Location of 8 bytes to encipher 
	 * 			UINT32_T *key Location of 128-bit key
	 * 
	 * Outputs: UINT32_T *d   Location to store enciphered data
	 * 
	 * Returns: None
	 ****************************************************************************/
	private int[] tkcipher_encipher(int s[], int k[]) {
		int d[] = new int[2];
		int a = TKCIPHER_FIX_ORDER_M(s[0]);
		int b = TKCIPHER_FIX_ORDER_M(s[1]);
		int sum = 0;
		byte numbits = TKCIPHER_NUMBITS;

		/* Encipher the 8 byte group */
		while (numbits-- > 0) {
			a += ((b << 4) ^ (b >>> 5)) + (b ^ sum) + k[sum & 3];
			sum += TKCIPHER_DELTA;
			b += ((a << 4) ^ (a >>> 5)) + (a ^ sum) + k[(sum >>> 11) & 3];
		}

		/* Copy results to destination */
		d[0] = TKCIPHER_FIX_ORDER_M(a);
		d[1] = TKCIPHER_FIX_ORDER_M(b);
		return d;
	}

}
