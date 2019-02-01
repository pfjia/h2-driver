package core;

import org.h2.command.CommandInterface;
import org.h2.message.TraceObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author pfjia
 * @since 2019/1/29 8:36
 */
public class Test {

    public static String method2String(Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        String returnTypeName = method.getReturnType().getSimpleName();
        String methodName = method.getName();
        Parameter[] parameters = method.getParameters();
        Class<?>[] exceptionClasses = method.getExceptionTypes();

        stringBuilder.append(returnTypeName).append(" ")
                .append(methodName).append("(");
        for (Parameter parameter : parameters) {
            stringBuilder.append(parameter.getType().getSimpleName()).append(" ")
                    .append(parameter.getName()).append(", ");
        }
        if (parameters.length > 0) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        stringBuilder.append(") ");
        if (exceptionClasses.length > 0) {
            stringBuilder.append("throws ");
            for (Class<?> exceptionClass : exceptionClasses) {
                stringBuilder.append(exceptionClass.getSimpleName())
                        .append(", ");
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        stringBuilder.append(";");
        return stringBuilder.toString();
    }


    public static String field2String(Field field) {
        int modifier = field.getModifiers();
        return Modifier.toString(modifier) + " " + field.getType().getSimpleName() + " " + field.getName();
    }

    public static String classDeclaredMethods2Table(Class<?> aClass) {
        Method[] methods = aClass.getDeclaredMethods();
        StringBuilder result = new StringBuilder();
        result.append("|").append("方法").append("|").append("说明").append("|").append("h2实现").append("|")
                .append("\n");
        result.append("|---------|--|--|")
                .append("\n");
        for (Method method : methods) {
            StringBuilder sb = new StringBuilder();
            sb.append("|")
                    .append(method2String(method))
                    .append("|")
                    .append("|")
                    .append("\n");
            result.append(sb);
        }
        return result.toString();
    }


    public static String classDeclaredFields2Table(Class<?> aClass) {
        Field[] fields = Arrays.stream(aClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);
        StringBuilder result = new StringBuilder();
        result.append("|").append("方法").append("|").append("说明").append("|").append("h2实现").append("|")
                .append("\n");
        result.append("|---------|--|--|")
                .append("\n");
        for (Field field : fields) {
            StringBuilder sb = new StringBuilder();
            sb.append("|")
                    .append(field2String(field))
                    .append("|")
                    .append("|")
                    .append("\n");
            result.append(sb);
        }
        return result.toString();
    }

    public static void main(String[] args) throws IllegalAccessException {
//        String s = classDeclaredFields2Table(CommandInterface.class);
//        System.out.println(s);

        Class<?> aClass = TraceObject.class;
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = Arrays.stream(aClass.getDeclaredFields())
                .filter(new Predicate<Field>() {
                    @Override
                    public boolean test(Field field) {
                        return Modifier.isStatic(field.getModifiers());
                    }
                }).toArray(Field[]::new);
        for (Field field : fields) {
            field.setAccessible(true);
            StringBuilder sb = new StringBuilder();
            sb.append(field.getName()).append("(")
                    .append(field.get(CommandInterface.class))
                    .append("),")
                    .append("\n");
            stringBuilder.append(sb);
        }
        System.out.println(stringBuilder);
    }
}
