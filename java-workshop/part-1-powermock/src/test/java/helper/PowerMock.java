package helper;

import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * Simulates PowerMock's private method mocking using Mockito spy + reflection.
 * <p>
 * In a real legacy codebase, you'd use PowerMock to intercept private methods.
 * Here we use package-private methods + spy as a practical equivalent.
 *
 * <pre>
 * OrderService spy = PowerMock.spyOn(new OrderService());
 * PowerMock.whenPrivate(spy, "reserveStock").thenReturn(List.of(...));
 * PowerMock.whenPrivate(spy, "releaseStock").thenDoNothing();
 * </pre>
 */
public class PowerMock {

    /**
     * Creates a Mockito spy on the given instance.
     */
    public static <T> T spyOn(T instance) {
        return Mockito.spy(instance);
    }

    /**
     * Prepares a stub for a package-private method identified by name.
     * Mimics PowerMock's {@code PowerMockito.when(instance, "methodName")}.
     *
     * @param spy        a spy created with {@link #spyOn(Object)}
     * @param methodName the name of the method to mock
     */
    public static <T> PrivateMethodStub<T> whenPrivate(T spy, String methodName) {
        return new PrivateMethodStub<>(spy, methodName);
    }

    /**
     * Invokes a package-private method by name using reflection.
     * Mimics PowerMock's {@code Whitebox.invokeMethod(instance, "methodName", args)}.
     *
     * @param instance   the object to invoke the method on
     * @param methodName the name of the method to call
     * @param args       the arguments to pass
     * @return the method's return value (or null for void)
     */
    @SuppressWarnings("unchecked")
    public static <R> R invokePrivate(Object instance, String methodName, Object... args) {
        try {
            Class<?> clazz = instance.getClass();
            Method method = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Method '" + methodName + "' not found in " + clazz.getSimpleName()));
            method.setAccessible(true);
            return (R) method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method: " + methodName, e);
        }
    }

    public static class PrivateMethodStub<T> {
        private final T spy;
        private final String methodName;

        PrivateMethodStub(T spy, String methodName) {
            this.spy = spy;
            this.methodName = methodName;
        }

        /**
         * Stubs the method to return the given value (for non-void methods).
         */
        public void thenReturn(Object returnValue) {
            try {
                Method method = findMethod();
                method.setAccessible(true);
                // IMPORTANT: matchers must be created AFTER entering stubbing context
                doReturn(returnValue).when(spy);
                Object[] matchers = buildMatchers(method);
                method.invoke(spy, matchers);
            } catch (Exception e) {
                throw new RuntimeException("Failed to mock method: " + methodName, e);
            }
        }

        private Method findMethod() {
            Class<?> current = spy.getClass();
            // Walk up past Mockito proxy to the real class
            while (current.getName().contains("$MockitoMock$")
                    || current.getName().contains("$$")) {
                current = current.getSuperclass();
            }
            final Class<?> targetClass = current;
            return Arrays.stream(targetClass.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Method '" + methodName + "' not found in " + targetClass.getSimpleName()));
        }

        private Object[] buildMatchers(Method method) {
            Class<?>[] paramTypes = method.getParameterTypes();
            Object[] matchers = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                matchers[i] = any(paramTypes[i]);
            }
            return matchers;
        }
    }
}

