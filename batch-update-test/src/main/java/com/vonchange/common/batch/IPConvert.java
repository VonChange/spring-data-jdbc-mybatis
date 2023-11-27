package com.vonchange.common.batch;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class IPConvert extends ClassicConverter {
	private static Logger logger = LoggerFactory.getLogger(IPConvert.class);

	public static  String  IP_AND_ADDRESS;

	static {
		try {
			
			Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
			ArrayList<String> ipv4Result = new ArrayList<>();
			
			while (enumeration.hasMoreElements()) {
				
				final NetworkInterface networkInterface = enumeration.nextElement();
				final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
				
				while (en.hasMoreElements()) {
					
					final InetAddress address = en.nextElement();
					
					if (!address.isLoopbackAddress()&&address instanceof Inet4Address) {
							ipv4Result.add(normalizeHostAddress(address));
					}
					
				}
			}
			// prefer ipv4
			if (!ipv4Result.isEmpty()) {
				IP_AND_ADDRESS = ipv4Result.get(0);
			} else {
				// If failed to find,fall back to localhost
				final InetAddress localHost = InetAddress.getLocalHost();
				IP_AND_ADDRESS = normalizeHostAddress(localHost);
			}
		} catch (SocketException e) {
			logger.error("SocketException{}",e);
		} catch (UnknownHostException e) {
			logger.error("UnknownHostException{}",e);
		}
	}

	@Override
	public String convert(ILoggingEvent event) {
		return IP_AND_ADDRESS;
	}

	public static String normalizeHostAddress(final InetAddress localHost) {

		return localHost.getHostAddress() + " | " + localHost.getHostName();
	}

}

