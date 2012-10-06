package com.googlecode;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Utility class for clipboard manipulation
 * 
 * @author GauravKumar
 * @date july 2011
 */
public class ClipboardRdc implements ClipboardOwner {

	public static void main(String[] args) {
		new UserInterface();
	}

	/**
	 * Copies contents of passed string to system clipboard
	 * 
	 * @param text
	 *            String to be copied to clipboard
	 */
	public static void copy(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(text), null);
	}

	/**
	 * Fetches the contents of system clipboard and returns if it is a String
	 * 
	 * @return Contents of clipboard if string, otherwise null
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	public static String paste() throws UnsupportedFlavorException, IOException {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return (String) contents.getTransferData(DataFlavor.stringFlavor);
		} else
			return null;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
}
