void* pass_object(void* objArg, void* (*getObject)(), void* (*verifyObject)(void*, void*))
{
	void* objLocal = getObject();
	return verifyObject(objArg, objLocal);
}
