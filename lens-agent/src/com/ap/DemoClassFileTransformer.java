package com.ap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;

public class DemoClassFileTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] bytecode = classfileBuffer;
        String fullClassName;
        boolean inPackage;
        try {
            String instrFile = System.getProperty("INSTR_FILE");
            if(instrFile == null) {
                instrFile = System.getProperty("user.home") + System.getProperty("file.separator") + "instr_file.txt";
            }
            List<String> packageList = ReadFile.readLines(instrFile);
            ClassPool cPool = ClassPool.getDefault();
            CtClass ctClass = cPool.makeClass(new ByteArrayInputStream(bytecode));
            CtMethod[] ctClassMethods = ctClass.getDeclaredMethods();
            for (CtMethod ctClassMethod : ctClassMethods) {
                fullClassName = ctClassMethod.getDeclaringClass().getName();
                if(isClassInPackage(fullClassName, packageList)) {
                    System.out.println( "[LENS-AGENT]: " + ctClassMethod.getLongName() );
                    //System.out.println( "[LENS-AGENT]: " + ctClassMethod.getDeclaringClass().getName() );

                    //Log execution time
                    //ctClassMethod.addLocalVariable("elapsedTime", CtClass.longType);
                    //ctClassMethod.insertBefore("elapsedTime = System.currentTimeMillis();");
                    //ctClassMethod.insertAfter("{elapsedTime = System.currentTimeMillis() - elapsedTime;"
                    //        + "System.out.println(\"Method Executed in ms: \" + elapsedTime);}");

                    //ExprEditor instrumentationExpressionEditor = new DemoExpressionEditor();
                    //ctClassMethod.instrument(instrumentationExpressionEditor);
                    bytecode = ctClass.toBytecode();
                }
            }

        } catch (IOException e) {
            throw new IllegalClassFormatException(e.getMessage());
        } catch (RuntimeException e) {
            throw new IllegalClassFormatException(e.getMessage());
        } catch (CannotCompileException e) {
            throw new IllegalClassFormatException(e.getMessage());
        }
        return bytecode;
    }

    private boolean isClassInPackage(String fullName, List<String> packages) {
        //System.out.println( "from agent - class: " + fullName );
        boolean result = false;
        for(String package1 : packages) {
            if(fullName.startsWith(package1)) {
                result = true;
                //System.out.println("Found a package to instrument...");
                break;
            }
        }
        return result;
    }
}