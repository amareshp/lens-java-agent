package com.ap;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class DemoClassFileTransformer3 implements ClassFileTransformer {
  private ClassPool pool;
  private String previousMethod;

  public DemoClassFileTransformer3() {
    pool = ClassPool.getDefault();
  }

  public byte[] transform(ClassLoader loader, String className,
      Class classBeingRedefined, ProtectionDomain protectionDomain,
      byte[] classfileBuffer) throws IllegalClassFormatException {
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
      List<String> trackList = ReadFile.readLines(instrFile);

      CtClass cclass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
      if (!cclass.isFrozen()) {
        for (CtMethod currentMethod : cclass.getDeclaredMethods()) {
          int paramCount = currentMethod.getParameterTypes().length;
          if (isNameInList(currentMethod.getLongName(), trackList)) {
            currentMethod.insertBefore(createJavaString(currentMethod, previousMethod, paramCount));
            //System.out.println("caller: " + className);
          }
          previousMethod = currentMethod.getLongName();
        }
        return cclass.toBytecode();
      }
    } catch (Exception e) {
      //e.printStackTrace();
    }
    return null;
  }

  private String createJavaString(CtMethod currentMethod, String previousMethod, int paramCount) {
    StringBuilder sb = new StringBuilder();
    sb.append("{StringBuilder sb = new StringBuilder");
    sb.append("(\"A call was made to method: \");");
    sb.append("sb.append(\"");
    sb.append(currentMethod.getLongName());
    sb.append("\");");
    sb.append("System.out.println(sb.toString()");
    for(int i=1; i<=paramCount; i++) {
      sb.append(" + \" [\" + $" + i + " + \"]\"");
    }
    sb.append("); new Throwable().printStackTrace();}");

    System.out.println("injected code: " + sb.toString());

    return sb.toString();
  }

  private String listToCsv(List list) {
    StringBuilder sb = new StringBuilder();
    if(list == null || list.size() == 0) {
      return "";
    } else {
      for(Object obj : list) {
        if(obj instanceof javassist.bytecode.CodeAttribute) {
          javassist.bytecode.CodeAttribute codeAttr = (javassist.bytecode.CodeAttribute) obj;
        }
        sb.append(obj.toString() + ", ");
      }
    }
    String result = sb.toString();
    result = result.substring(0, (result.length()-2));
    return result;
  }

  private boolean isNameInList(String name, List<String> trackList) {
    //System.out.println( "from agent - class: " + fullName );
    boolean result = false;
    for(String package1 : trackList) {
      if(name.contains(package1)) {
        result = true;
        //System.out.println("Found a package to instrument...");
        break;
      }
    }
    return result;
  }

}