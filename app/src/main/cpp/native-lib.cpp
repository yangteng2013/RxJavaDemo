#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_noe_rxjava_JNIActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from ganji";
    return env->NewStringUTF(hello.c_str());
}
