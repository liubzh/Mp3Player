#define TAG "native_libmad"

#include <android/log.h>
#include <cstring>
#include <jni.h>
#include <string>

/* Header for class com_binzosoft_audiotrackdemo_NativeMP3Decoder */
#ifndef _Included_com_binzosoft_audiotrackdemo_NativeMP3Decoder

#define _Included_com_binzosoft_audiotrackdemo_NativeMP3Decoder
#ifdef __cplusplus

extern "C" {
#endif

extern int NativeMP3Decoder_readSamples(short *target, int size);
extern void NativeMP3Decoder_closeAduioFile();
extern int NativeMP3Decoder_getAduioSamplerate();
extern int NativeMP3Decoder_init(char *filepath, unsigned long start);

/*
 * Class:     com_binzosoft_audiotrackdemo_NativeMP3Decoder
 * Method:    initAudioPlayer
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_binzosoft_audiotrackdemo_NativeMP3Decoder_initAudioPlayer
  (JNIEnv *env, jobject obj, jstring file, jint startAddr) {
    char *fileString = (char *)env->GetStringUTFChars(file, JNI_FALSE);

    return NativeMP3Decoder_init(fileString, startAddr);
}

/*
 * Class:     com_binzosoft_audiotrackdemo_NativeMP3Decoder
 * Method:    getAudioBuf
 * Signature: ([SI)I
 */
JNIEXPORT jint JNICALL Java_com_binzosoft_audiotrackdemo_NativeMP3Decoder_getAudioBuf
  (JNIEnv *env, jobject obj, jshortArray audioBuf, jint len) {
    int bufsize = 0;
    int ret = 0;
    if (audioBuf != NULL) {
        bufsize = env->GetArrayLength(audioBuf);
        jshort *_buf = env->GetShortArrayElements(audioBuf, 0);
        memset(_buf, 0, bufsize * 2);
        ret = NativeMP3Decoder_readSamples(_buf, len);
        env->ReleaseShortArrayElements(audioBuf, _buf, 0);
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "getAudio failed");
    }
    return ret;
}

/*
 * Class:     com_binzosoft_audiotrackdemo_NativeMP3Decoder
 * Method:    closeAduioFile
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_binzosoft_audiotrackdemo_NativeMP3Decoder_closeAduioFile
  (JNIEnv *env, jobject obj) {
    NativeMP3Decoder_closeAduioFile();
}

/*
 * Class:     com_binzosoft_audiotrackdemo_NativeMP3Decoder
 * Method:    getAudioSamplerate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_binzosoft_audiotrackdemo_NativeMP3Decoder_getAudioSamplerate
  (JNIEnv *env, jobject obj) {
    return NativeMP3Decoder_getAduioSamplerate();
}

JNIEXPORT jstring JNICALL Java_com_binzosoft_audiotrackdemo_NativeMP3Decoder_stringFromJNI( JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

#ifdef __cplusplus
}
#endif
#endif
