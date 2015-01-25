package com.futonredemption.makemotivator.util;

public class StringUtils {

	public static String capitalizeFirstLetterOfEachWord(String content) {
		final StringBuilder sb = new StringBuilder();
		boolean pendingCap = true;
		final String str = content.trim();
		final int len = str.length();
		if(len > 1) {
			for(int i = 1; i < len; i++) {
				final char prev = str.charAt(i - 1);
				final char cur = str.charAt(i);
				if(pendingCap && Character.isLetter(prev)) {
					sb.append(Character.toUpperCase(prev));
					pendingCap = false;
				} else {
					sb.append(Character.toLowerCase(prev));
				}

				if(! Character.isLetterOrDigit(cur)) {
					pendingCap = true;
				}
			}

			final char lastChar = str.charAt(len - 1);
			if(pendingCap) {
				sb.append(Character.toUpperCase(lastChar));
			} else {
				sb.append(Character.toLowerCase(lastChar));
			}
		} else {
			sb.append(str.toUpperCase());
		}


		return sb.toString();
     }

	public static String alphaNumeric(final String str) {
		final StringBuilder sb = new StringBuilder();

		final int len = str.length();
		for(int i = 0; i < len; i++) {
			final char cur = str.charAt(i);
			if(Character.isLetterOrDigit(cur) || Character.isWhitespace(cur)) {
				sb.append(cur);
			}
		}

		return sb.toString();
	}
}
