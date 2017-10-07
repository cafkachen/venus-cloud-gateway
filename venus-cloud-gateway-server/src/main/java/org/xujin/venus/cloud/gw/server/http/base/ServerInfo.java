package org.xujin.venus.cloud.gw.server.http.base;

import java.net.InetSocketAddress;

/**
 * 
 * @author xujin
 *
 */
public class ServerInfo {
	InetSocketAddress inetAddress;
	Integer holdingTimeout;

	public ServerInfo(InetSocketAddress inetSocketAddress) {
		super();
		this.inetAddress = inetSocketAddress;
	}

	public ServerInfo(String host, int port) {
		super();
		this.inetAddress = new InetSocketAddress(host, port);
	}

	@Override
	public String toString() {
		return "ServerInfo [" + inetAddress.toString() + "]";
	}

	public String getHost() {
		return inetAddress.getHostName();
	}

	public int getPort() {
		return inetAddress.getPort();
	}

	public Integer getHoldingTimeout() {
		return holdingTimeout;
	}

	public void setHoldingTimeout(Integer holdingTimeout) {
		this.holdingTimeout = holdingTimeout;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ServerInfo that = (ServerInfo) o;
		return (inetAddress == null ? that.inetAddress == null
				: inetAddress.equals(that.inetAddress));
	}

	@Override
	public int hashCode() {
		return inetAddress != null ? inetAddress.hashCode() : 0;
	}

	public InetSocketAddress getInetAddress() {
		return inetAddress;
	}
}
