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
	whileLoop_0:
	ldc 5
	ifle endWhile_0
	.var 5 is variable_5 I
	ldc 5
	istore 5
	iload 5
	ldc 1
	isub
	istore 5
	iload 5
	istore 4
	.var 6 is variable_6 D
	dload 1
	dstore 6
	dload 6
	ldc2_w 2.5000
	dadd
	dstore 6
	dload 6
	dstore 1
	goto whileLoop_0
	endWhile_0:
	.var 8 is variable_8 I
	dload 1
	d2i
	istore 8
	iload 8
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
