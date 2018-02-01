package com.ap;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class DemoClassFileTransformer2 implements ClassFileTransformer {
    Set<String> classSet = new LinkedHashSet<>();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        byte[] bytecode = classfileBuffer;
        final String fullMethodName;
        boolean inPackage;
        FileWriter fw;
        BufferedWriter bw;
        PrintWriter out = null;
        try {
            String instrFile = System.getProperty("INSTR_FILE");
            String logToFile = System.getProperty("LOG_FILE");
            if(instrFile == null) {
                instrFile = System.getProperty("user.home") + System.getProperty("file.separator") + "instr_file.txt";
            }
            if(logToFile == null) {
                logToFile = System.getProperty("user.home") + System.getProperty("file.separator") + "lens_log_file.log";
            }
            fw = new FileWriter(logToFile, true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);

            List<String> packageList = ReadFile.readLines(instrFile);
            ClassPool cPool = ClassPool.getDefault();
            CtClass ctClass = cPool.makeClass(new ByteArrayInputStream(bytecode));
            CtMethod[] ctClassMethods = ctClass.getMethods();
            for (final CtMethod ctClassMethod : ctClassMethods) {
              //fullMethodName = ctClassMethod.getLongName();
                if(isClassInPackage(ctClassMethod.getLongName(), packageList)) {
                  //System.out.println("method: " + ctClassMethod.getLongName());
                    //System.out.println( "[LENS-AGENT]: " + ctClassMethod.getLongName() );

                    //System.out.println("[LENS-AGENT]: " + ctClassMethod.getLongName());
                    //out.println( "[LENS-AGENT]: " + ctClassMethod.getLongName() );
                    //classSet.add(ctClassMethod.getDeclaringClass().getName());
                    //System.out.println( "[LENS-AGENT]: " + ctClassMethod.getDeclaringClass().getName() );

                  //m.getEnclosingClass().getSimpleName() + " - " + m.getMethod().getLongName()
                    ctClassMethod.instrument(new ExprEditor() {
                        public void edit(final MethodCall m) throws CannotCompileException {
                          try {
                              List<javassist.bytecode.CodeAttribute> attributes = ctClassMethod.getMethodInfo().getAttributes();
                              StringBuilder attrStr = new StringBuilder();
                              for (javassist.bytecode.CodeAttribute attribute : attributes) {
                                  attrStr.append( " " + new String(m.getMethodName()) + " line: " + m.getLineNumber() + " ");
                              }
                            m.replace("{" +
                                "long startMs = System.currentTimeMillis(); " +
                                "$_ = $proceed($$); " +
                                "long endMs = System.currentTimeMillis();" +
                                "if((endMs-startMs) > 100) System.out.println(\"" + ctClassMethod.getLongName() + attrStr + " Executed in ms: \" + (endMs-startMs) );" +
                                "}");
                          } catch (Exception ex) {
                            ex.printStackTrace();
                          }
                        }
                    });
                    //Log execution time
/*
                    ctClassMethod.addLocalVariable("elapsedTime", CtClass.longType);
                    ctClassMethod.insertBefore("elapsedTime = System.currentTimeMillis();");
                    ctClassMethod.insertAfter("{elapsedTime = System.currentTimeMillis() - elapsedTime;"
                        + "if(elapsedTime > 10) System.out.println(" + ctClassMethod.getName() + " + \" Method Executed in ms: \" + elapsedTime);}");
*/

                    //ExprEditor instrumentationExpressionEditor = new DemoExpressionEditor();
                    //ctClassMethod.instrument(instrumentationExpressionEditor);
                    bytecode = ctClass.toBytecode();
                    ctClass.detach();
                }
            }

        } catch (IOException e) {
            throw new IllegalClassFormatException(e.getMessage());
        } catch (RuntimeException e) {
            throw new IllegalClassFormatException(e.getMessage());
        } catch (CannotCompileException e) {
            throw new IllegalClassFormatException(e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            for(String cName : classSet) {
                //out.println(cName);
            }
            out.close();
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
