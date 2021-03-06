/**
 * Classes.java 9:22:44 AM Apr 23, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package org.cc.core.common;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.cc.core.asm.ClassReader;
import org.cc.core.asm.ClassVisitor;
import org.cc.core.asm.Label;
import org.cc.core.asm.MethodVisitor;
import org.cc.core.asm.Opcodes;
import org.cc.core.asm.Type;

/**
 * <p>
 * 基于asm的工具类
 * </p>
 * 
 * @author dixingxing
 * @date Apr 23, 2012
 */
public final class Classes {

	private Classes() {
	}

	/**
	 * 
	 * <p>
	 * 比较参数类型是否一致
	 * </p>
	 * 
	 * @param types
	 *            asm的类型({@link Type})
	 * @param clazzes
	 *            java 类型({@link Class})
	 * @return
	 */
	private static boolean sameType(Type[] types, Class<?>[] clazzes) {
		// 个数不同
		if (types.length != clazzes.length) {
			return false;
		}

		for (int i = 0; i < types.length; i++) {
			if (!Type.getType(clazzes[i]).equals(types[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * <p>
	 * 获取方法的参数名
	 * </p>
	 * 
	 * @param m
	 * @return
	 */
	public static String[] getMethodParamNames(final Method m) {
		final String[] paramNames = new String[m.getParameterTypes().length];
		final String n = m.getDeclaringClass().getName();
		ClassReader cr = null;
		try {
			cr = new ClassReader(n);
		} catch (IOException e) {
			e.printStackTrace();
			Exceptions.uncheck(e);
		}
		cr.accept(new ClassVisitor(Opcodes.ASM4) {
			@Override
			public MethodVisitor visitMethod(final int access,
					final String name, final String desc,
					final String signature, final String[] exceptions) {
				final Type[] args = Type.getArgumentTypes(desc);
				// 方法名相同并且参数个数相同
				if (!name.equals(m.getName())
						|| !sameType(args, m.getParameterTypes())) {
					return super.visitMethod(access, name, desc, signature,
							exceptions);
				}
				MethodVisitor v = super.visitMethod(access, name, desc,
						signature, exceptions);
				return new MethodVisitor(Opcodes.ASM4, v) {
					@Override
					public void visitLocalVariable(String name, String desc,
							String signature, Label start, Label end, int index) {
						int i = index - 1;
						// 如果是静态方法，则第一就是参数
						// 如果不是静态方法，则第一个是"this"，然后才是方法的参数
						if (Modifier.isStatic(m.getModifiers())) {
							i = index;
						}
						if (i >= 0 && i < paramNames.length) {
							paramNames[i] = name;
						}
						super.visitLocalVariable(name, desc, signature, start,
								end, index);
					}

				};
			}
		}, 0);
		return paramNames;
	}
	
	/**
	 * 
	 * <p>根据类名获取类的实例</p>
	 *
	 * @param <T>
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className) {
		T obj = null;
		try {
			obj = (T) Class.forName(className).newInstance();
		} catch (Exception e) {
			Exceptions.uncheck(e);
		}
		return obj;
	}

	public static void main(String[] args) throws SecurityException,
			NoSuchMethodException {
		String[] s = getMethodParamNames(Classes.class.getMethod(
				"getMethodParamNames", Method.class));
		System.out.println(Arrays.toString(s));

		s = getMethodParamNames(Classes.class.getDeclaredMethod("sameType",
				Type[].class, Class[].class));

		System.out.println(Arrays.toString(s));
	}

}
