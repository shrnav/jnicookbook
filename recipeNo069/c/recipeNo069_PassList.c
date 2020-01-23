#include <stdio.h>
#include "jni.h"
#include "recipeNo069_PassList.h"

JNIEXPORT void JNICALL Java_recipeNo069_PassList_passObjectsAsList
  (JNIEnv * env, jclass cls, jobject list) {

  // Let's get the size of the list
  jint list_size = (*env)->GetArrayLength(env, list);

  // for each index, let's iterate over the list of elements
  for(int i=0; i < list_size; i++) {

    // We are getting element at index 'i'
    jobject   obj        = (*env)->GetObjectArrayElement(env, list, i);

    // Now, let's get ID of the method that will provide us with the 'name'
    // that is stored inside SimpleBean. First of all, let's get the class
    // of the object
    jclass    cls_SimpleBean  = (*env)->GetObjectClass(env, obj);

    // And we can learn what is the ID of method 'getName'
    jmethodID fid_getName     = (*env)->GetMethodID( env, cls_SimpleBean, "getName", 
                                             "()Ljava/lang/String;");

    // Once we have everything, we can get the value of 'name' that is stored
    // inside 'SimpleBean'
    jobject   name            = (*env)->CallObjectMethod(env, obj, fid_getName);

    // after converting Java style String to C based array of characters
    const char *c_string = (*env)->GetStringUTFChars (env, name, 0);

    // we can print it on the stdout
    printf ("[name] = %s\n", c_string);

    // and release memory used for transfering data from Java to C
    (*env)->ReleaseStringUTFChars (env, name, c_string);
  }
}
