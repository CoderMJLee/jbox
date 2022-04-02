package io.github.codermjlee.common.util.binary;

public class CRC16s {

	public static int GENIBUS(int[] buffer) {
		int wCRCin = 0xFFFF;
		int wCPoly = 0x1021;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0xFFFF;

		return wCRCin;
	}

	/*
	 * CRC-CCITT (0xFFFF)
	 * CRC16_CCITT_FALSE：多项式x16+x12+x5+1（0x1021），初始值0xFFFF，低位在后，高位在前，结果与0x0000异或
	 *
	 */
	public static int CCITT_FALSE(int[] buffer) {
		int wCRCin = 0xFFFF;
		int wCPoly = 0x1021;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;

		return wCRCin;
	}

	public static int CCITT(byte[] buffer) {
		int wCRCin = 0xFFFF;
		int wCPoly = 0x1021;
		for (byte b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	public static int ARC(int[] buffer) {
		int wCRCin = revertBit(0x0000);
		int POLYNOMIAL = revertBit(0x8005); // 8005 逆Bit序 A001
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	public static int AUG_CCITT(int[] buffer) {
		int wCRCin = 0x1D0F;
		int wCPoly = 0x1021;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	public static int BUYPASS(int[] buffer) {
		int wCRCin = 0x0000;
		int wCPoly = 0x8005;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	public static int CDMA2000(int[] buffer) {
		int wCRCin = 0xFFFF;
		int wCPoly = 0xC867;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	public static int DDS_110(int[] buffer) {
		int wCRCin = 0x800D;
		int wCPoly = 0x8005;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	public static int DECT_R(int[] buffer) {
		int wCRCin = 0x0000;
		int wCPoly = 0x0589;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0001;
		return wCRCin;
	}

	public static int DECT_X(int[] buffer) {
		int wCRCin = 0x0000;
		int wCPoly = 0x0589;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	public static int DNP(int[] buffer) {
		int wCRCin = revertBit(0x0000);
		int POLYNOMIAL = revertBit(0x3D65);
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0xFFFF;
		return wCRCin;
	}

	public static int EN_13757(int[] buffer) {
		int wCRCin = 0x0000;
		int wCPoly = 0x3D65;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0xFFFF;
		return wCRCin;
	}

	public static int MAXIM(int[] buffer) {
		int wCRCin = revertBit(0x0000);
		int POLYNOMIAL = revertBit(0x8005);
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0xFFFF;
		return wCRCin;
	}

	// RIELLO
	public static int RIELLO(int[] buffer) {
		int wCRCin = revertBit(0xB2AA);
		int POLYNOMIAL = revertBit(0x1021);
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	// MCRF4XX
	public static int MCRF4XX(int[] buffer) {
		int wCRCin = revertBit(0xFFFF);
		int POLYNOMIAL = revertBit(0x1021);
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	// T10-DIF
	public static int T10_DIF(int[] buffer) {
		int wCRCin = 0x0000;
		int wCPoly = 0x8BB7;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	// TELEDISK
	public static int TELEDISK(int[] buffer) {
		int wCRCin = 0x0000;
		int wCPoly = 0xA097;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	// TMS37157
	public static int TMS37157(int[] buffer) {
		int wCRCin = revertBit(0x89EC);
		int POLYNOMIAL = revertBit(0x1021);

		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	// USB
	public static int USB(int[] buffer) {
		int wCRCin = revertBit(0xFFFF);
		int POLYNOMIAL = revertBit(0x8005);
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0xFFFF;
		return wCRCin;
	}

	// A
	public static int A(int[] buffer) {
		int wCRCin = revertBit(0xC6C6);
		int POLYNOMIAL = revertBit(0x1021);
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	// KERMIT
	public static int KERMIT(int[] buffer) {
		int wCRCin = revertBit(0x0000);
		int POLYNOMIAL = revertBit(0x1021);
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	public static int MODBUS(int[] buffer) {
		int wCRCin = revertBit(0xFFFF);
		int POLYNOMIAL = revertBit(0x8005);
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	// X-25
	public static int X_25(int[] buffer) {
		int wCRCin = revertBit(0xFFFF);
		int POLYNOMIAL = revertBit(0x1021);
		for (int b : buffer) {
			wCRCin ^= ((int) b & 0x00ff);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= POLYNOMIAL;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0xFFFF;
		return wCRCin;
	}

	// XMODEM
	public static int XMODEM(int[] buffer) {
		int wCRCin = 0x0000;
		int wCPoly = 0x1021;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	public static int _8005(int[] buffer) {
		int wCRCin = 0x0000;
		int wCPoly = 0x8005;
		for (int b : buffer) {
			for (int i = 0; i < 8; i++) {
				boolean bit = (((b & 0xFFFF) >> (7 - i) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit)
					wCRCin ^= wCPoly;
			}
		}
		wCRCin &= 0xffff;
		wCRCin ^= 0x0000;
		return wCRCin;
	}

	/**
	 * 翻转16位的高八位和低八位字节
	 *
	 * @param src 翻转数字
	 * @return 翻转结果
	 */
	private static int revert(int src) {
		int lowByte = (src & 0xFF00) >> 8;
		int highByte = (src & 0x00FF) << 8;
		return lowByte | highByte;
	}

	private static int revertBit(int src) {
		int tmp = 0;

		for (int i = 0; i < 16; i++) {
			tmp = tmp << 1;
			tmp = ((src >> i) & 0x0001) | tmp;
		}

        tmp &= 0xFFFF;
        return tmp;
    }

    public static int getCrc16(byte[] arr_buff, int len) {
//		int len = arr_buff.length;
        // 预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {
            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));
            for (j = 0; j < 8; j++) {
                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0) {
                    // 如果移出位为 1, CRC寄存器与多项式A001进行异或
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else
                    // 如果移出位为 0,再次右移一位
                    crc = crc >> 1;
            }
        }
        return crc;
    }
}
