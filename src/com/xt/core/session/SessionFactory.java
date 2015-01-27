package com.xt.core.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.xt.core.log.LogWriter;

public class SessionFactory {

	private final static Logger logger = Logger.getLogger(SessionFactory.class);

	private static SessionFactory instance = new SessionFactory();

	private Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	/**
	 * Session 的长度的默认值。需要×2
	 */
	private int sessionIdLength = 16;

	/**
	 * The default message digest algorithm to use if we cannot use the
	 * requested one.
	 */
	protected static final String DEFAULT_ALGORITHM = "MD5";

	/**
	 * Return the MessageDigest implementation to be used when creating session
	 * identifiers.
	 */
	protected MessageDigest digest = null;

	private Random random = null;

	private String algorithm = DEFAULT_ALGORITHM;

	private SessionFactory() {

	}

	static SessionFactory getInstance() {
		return instance;
	}

	public Session getSession(String sessionId) {
		Session session = null;
        if (sessionId == null || !sessions.containsKey(sessionId)) {
        	session = createSession();
        } else {
        	session = sessions.get(sessionId); 
        }
		return session;
	}

	synchronized public Session createSession() {
		BaseSession session = new BaseSession();
		String id = generateSessionId();
		session.setId(id);
		LogWriter.debug(logger, "generate new id=", id);
    	sessions.put(id, session);
		return session;
	}

	/**
	 * 此段代码来源于Tomcat 6.0.18 的源码。<br/>
	 *  Generate and return a new session
	 * identifier.
	 */
	protected synchronized String generateSessionId() {

		byte randomBytes[] = new byte[32];
		String jvmRoute = null; // String.valueOf(System.currentTimeMillis());
								// // getJvmRoute();
		String result = null;

		// Render the result as a String of hexadecimal digits
		StringBuffer buffer = new StringBuffer();
		do {
			int resultLenBytes = 0;
			if (result != null) {
				// 存在重复，重新计算
				buffer = new StringBuffer();

			}

			while (resultLenBytes < this.sessionIdLength) {
				getRandomBytes(randomBytes);
				randomBytes = getDigest().digest(randomBytes);
				for (int j = 0; j < randomBytes.length
						&& resultLenBytes < this.sessionIdLength; j++) {
					byte b1 = (byte) ((randomBytes[j] & 0xf0) >> 4);
					byte b2 = (byte) (randomBytes[j] & 0x0f);
					if (b1 < 10)
						buffer.append((char) ('0' + b1));
					else
						buffer.append((char) ('A' + (b1 - 10)));
					if (b2 < 10)
						buffer.append((char) ('0' + b2));
					else
						buffer.append((char) ('A' + (b2 - 10)));
					resultLenBytes++;
				}
			}
			if (jvmRoute != null) {
				buffer.append('.').append(jvmRoute);
			}
			result = buffer.toString();
		} while (sessions.containsKey(result));
		return (result);

	}

	private static long seed = System.currentTimeMillis();

	protected void getRandomBytes(byte bytes[]) {
		if (this.random == null) {
			// Generate a byte array containing a session identifier
			try {
				random = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
				LogWriter.warn(logger, "算法[SHA1PRNG]不存在。", e);
				random = new Random(seed);
			}
		}
		random.nextBytes(bytes);
	}

	/**
	 * Return the MessageDigest object to be used for calculating session
	 * identifiers. If none has been created yet, initialize one the first time
	 * this method is called.
	 */
	public synchronized MessageDigest getDigest() {
		if (this.digest == null) {
			try {
				this.digest = MessageDigest.getInstance(algorithm);
			} catch (NoSuchAlgorithmException e) {
				LogWriter.warn(logger, String.format("无此算法[%s]", algorithm), e);
				try {
					this.digest = MessageDigest.getInstance(DEFAULT_ALGORITHM);
				} catch (NoSuchAlgorithmException f) {
					LogWriter.error(logger, String.format("无此算法[%s]",
							DEFAULT_ALGORITHM), e);
					this.digest = null;
				}
			}

		}
		return (this.digest);

	}

}
