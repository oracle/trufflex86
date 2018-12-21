#ifndef __TRUFFLENFI_H__
#define __TRUFFLENFI_H__

typedef void *TruffleContext;
typedef long TruffleObject;

#ifdef __cplusplus
typedef __TruffleEnv TruffleEnv;
#else
typedef const struct __TruffleNativeAPI *TruffleEnv;
#endif

struct __TruffleNativeAPI {
	TruffleContext *(*getTruffleContext)(TruffleEnv *env);
	TruffleObject (*newObjectRef)(TruffleEnv *env, TruffleObject object);
	void (*releaseObjectRef)(TruffleEnv *env, TruffleObject object);
	TruffleObject (*releaseAndReturn)(TruffleEnv *env, TruffleObject object);
	int (*isSameObject)(TruffleEnv *env, TruffleObject object1, TruffleObject object2);
	void (*newClosureRef)(TruffleEnv *env, void *closure);
	void (*releaseClosureRef)(TruffleEnv *env, void *closure);
	TruffleObject (*getClosureObject)(TruffleEnv *env, void *closure);
};

struct __TruffleEnv {
	const struct __TruffleNativeAPI *functions;

#ifdef __cplusplus
	TruffleContext *getTruffleContext() {
		return functions->getTruffleContext(this);
	}

	TruffleObject newObjectRef(TruffleObject object) {
		return functions->newObjectRef(this, object);
	}

	void releaseObjectRef(TruffleObject object) {
		functions->releaseObjectRef(this, object);
	}

	TruffleObject releaseAndReturn(TruffleObject object) {
		return functions->releaseAndReturn(this, object);
	}

	int isSameObject(TruffleObject object1, TruffleObject object2) {
		return functions->isSameObject(this, object1, object2);
	}

	template<class T> void newClosureRef(T *closure) {
		functions->newClosureRef(this, (void*) closure);
	}

	template<class T> void releaseClosureRef(T *closure) {
		functions->releaseClosureRef(this, (void*) closure);
	}

	template<class T> T *dupClosureRef(T *closure) {
		functions->newClosureRef(this, (void*) closure);
		return closure;
	}

	template<class T> TruffleObject getClosureObject(T *closure) {
		return functions->getClosureObject(this, (void*) closure);
	}
#endif
};

#endif
