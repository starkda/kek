.class public Kek
.super java/lang/Object


.method public static simpleFunc(IDZ)I
	.limit stack 50
	.limit locals 50
	.var 0 is variable_0 I
	.var 1 is variable_1 D
	.var 3 is variable_3 Z
	.var 4 is variable_4 I
	ldc 5
	istore 4
	.var 5 is variable_5 [I
	ldc 6
	newarray int
	astore 5
	.var 6 is variable_6 I
	ldc 0
	istore 6
	loopBody_0:
	aload 5
	iload 6
	iload 6
	iastore
	iinc 6 1
	checkCondition_0:
	iload 6
	iload 4
	if_icmpgt endFor_0
	goto loopBody_0
	endFor_0:
	aload 5
	ldc 2
	iaload
	.var 7 is variable_7 I
	istore 7
	iload 7
	ireturn
.end method


.method public static entryPoint()V
	.limit stack 50
	.limit locals 50
	return
.end method


.method public static main([Ljava/lang/String;)V
	.limit stack 50
	.limit locals 50
	invokestatic Kek/entryPoint()V
	return
.end method
