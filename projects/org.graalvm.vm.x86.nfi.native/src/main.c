#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include <unistd.h>
#include <errno.h>
#include <signal.h>

#define	SYS_interop_init	0xC0DE0000
#define	SYS_interop_error	0xC0DE0001

#define	STACK_SIZE		(4 * 1024 * 1024)	// 4MB

typedef void* TruffleContext;
typedef void* TruffleObject;
typedef const struct TruffleNativeAPI *TruffleEnv;

struct TruffleNativeAPI {
	TruffleContext *(*getTruffleContext)(TruffleEnv *env);
	TruffleObject (*newObjectRef)(TruffleEnv *env, TruffleObject object);
	void (*releaseObjectRef)(TruffleEnv *env, TruffleObject object);
	TruffleObject (*releaseAndReturn)(TruffleEnv *env, TruffleObject object);
	int (*isSameObject)(TruffleEnv *env, TruffleObject object1, TruffleObject object2);
	void (*newClosureRef)(TruffleEnv *env, void *closure);
	void (*releaseClosureRef)(TruffleEnv *env, void *closure);
	TruffleObject (*getClosureObject)(TruffleEnv *env, void *closure);
};

static TruffleContext* getTruffleContext(TruffleEnv* env);
static TruffleObject newObjectRef(TruffleEnv *env, TruffleObject object);
static void releaseObjectRef(TruffleEnv *env, TruffleObject object);
static TruffleObject releaseAndReturn(TruffleEnv *env, TruffleObject object);
static int isSameObject(TruffleEnv *env, TruffleObject object1, TruffleObject object2);
static void newClosureRef(TruffleEnv *env, void *closure);
static void releaseClosureRef(TruffleEnv *env, void *closure);
static TruffleObject getClosureObject(TruffleEnv *env, void *closure);

static const struct TruffleNativeAPI native_api = {
	.getTruffleContext = getTruffleContext,
	.newObjectRef = newObjectRef,
	.releaseObjectRef = releaseObjectRef,
	.releaseAndReturn = releaseAndReturn,
	.isSameObject = isSameObject,
	.newClosureRef = newClosureRef,
	.releaseClosureRef = releaseClosureRef,
	.getClosureObject = getClosureObject
};

static const TruffleEnv native_env = &native_api;

/* interop functions for native library access */
void report_error(const char* msg)
{
	if(syscall(SYS_interop_error, msg) == -1) {
		if(errno == ENOSYS) {
			fprintf(stderr, "Interop is not supported!\n");
			fprintf(stderr, "Message was: %s\n", msg);
		}
	}
}

void* load_library(const char* name)
{
	void* handle = dlopen(name, RTLD_LAZY);
	if(!handle) {
		const char* error = dlerror();
		report_error(error);
		return NULL;
	} else {
		return handle;
	}
}

void release_library(void* handle)
{
	dlclose(handle);
}

void* get_symbol(void* handle, const char* name)
{
        char* error;

	dlerror(); /* Clear any existing error */

	void* func = dlsym(handle, name);

	error = dlerror();
	if(error != NULL) {
		report_error(error);
		return NULL;
	} else {
		return func;
	}
}

/* NFI TruffleEnv implementation */
static TruffleContext* getTruffleContext(TruffleEnv* env)
{
	return NULL;
}

static TruffleObject newObjectRef(TruffleEnv *env, TruffleObject object)
{
	return object;
}

static void releaseObjectRef(TruffleEnv *env, TruffleObject object)
{
	/* nothing */
}

static TruffleObject releaseAndReturn(TruffleEnv *env, TruffleObject object)
{
	return object;
}

static int isSameObject(TruffleEnv *env, TruffleObject object1, TruffleObject object2)
{
	return object1 == object2;
}

static void newClosureRef(TruffleEnv *env, void *closure)
{
	/* nothing */
}

static void releaseClosureRef(TruffleEnv *env, void *closure)
{
	/* nothing */
}

static TruffleObject getClosureObject(TruffleEnv *env, void *closure)
{
	return closure;
}

int main(void)
{
	stack_t ss;

	ss.ss_sp = malloc(STACK_SIZE);
	ss.ss_flags = 0;
	ss.ss_size = STACK_SIZE;

	if(ss.ss_sp == NULL) {
		char buf[256];
		sprintf(buf, "cannot allocate memory: %s", strerror(errno));
		report_error(buf);
		return EXIT_FAILURE;
	}

	if(sigaltstack(&ss, NULL) < 0) {
		char buf[256];
		sprintf(buf, "sigaltstack failed: %s", strerror(errno));
		report_error(buf);
		return EXIT_FAILURE;
	}

	if(syscall(SYS_interop_init, load_library, release_library, get_symbol, &native_env) == -1) {
		if(errno == ENOSYS) {
			fprintf(stderr, "Interop is not supported!\n");
			return EXIT_FAILURE;
		}
	}

        return EXIT_SUCCESS;
}
