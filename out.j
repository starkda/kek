.class public Kek
.super java/lang/Object


.method public static simpleFunc(IDZ)I
	.limit stack 50
	.limit locals 50
	.var 0 is variable_0 I
	.var 1 is variable_1 D
	.var 3 is variable_3 Z
	.var 4 is variable_4 D
	iload 0
	i2d
	dstore 4
	.var 6 is variable_6 D
	dload 4
	dstore 6
	dload 6
	dload 1
	fadd
	fadd
	dstore 6
	.var 9 is variable_9 I
	dload 6
	d2i
	istore 9
	.var 8 is variable_8 I
	iload 9
	istore 8
	return
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
