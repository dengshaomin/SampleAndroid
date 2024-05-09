#include <jni.h>
#include <string>

int add(int a, int b);

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    int c = add(1, 2);
    std::string result = std::to_string(c);
    return env->NewStringUTF(result.c_str());
}