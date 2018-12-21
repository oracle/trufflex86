#include <stdint.h>
#include "trufflenfi.h"

#define GEN_NUMERIC_TEST(name, type) \
type increment_##name(type arg) { \
	return arg + 1; \
} \
\
type callback_##name(type (*fn)(type), type arg) { \
	return fn(arg + 1) * 2; \
} \
\
typedef type (*fnptr_##name)(type); \
\
fnptr_##name callback_ret_##name() { \
	return increment_##name; \
} \
\
type pingpong_##name(TruffleEnv *env, fnptr_##name (*wrapFn)(TruffleEnv *env, fnptr_##name), type arg) { \
	fnptr_##name wrapped = wrapFn(env, increment_##name); \
	int ret = wrapped(arg + 1) * 2; \
	(*env)->releaseClosureRef(env, wrapped); \
	return ret; \
}

GEN_NUMERIC_TEST(SINT8, int8_t)
GEN_NUMERIC_TEST(UINT8, uint8_t)
GEN_NUMERIC_TEST(SINT16, int16_t)
GEN_NUMERIC_TEST(UINT16, uint16_t)
GEN_NUMERIC_TEST(SINT32, int32_t)
GEN_NUMERIC_TEST(UINT32, uint32_t)
GEN_NUMERIC_TEST(SINT64, int64_t)
GEN_NUMERIC_TEST(UINT64, uint64_t)
GEN_NUMERIC_TEST(FLOAT, float)
GEN_NUMERIC_TEST(DOUBLE, double)
GEN_NUMERIC_TEST(POINTER, intptr_t)
