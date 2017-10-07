
package org.xujin.venus.cloud.gw.server.compiler;
/**
 * 
 * @author xujin
 *
 */
public interface Compiler {

	/**
	 * Compile java source code.
	 * 
	 * @param code Java source code
	 * @param classLoader TODO
	 * @return Compiled class
	 */
	Class<?> compile(String code, ClassLoader classLoader);

}
