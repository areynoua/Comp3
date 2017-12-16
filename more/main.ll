; External declaration for ramdomization
declare i32 @rand()
declare i32 @time(i32*)
declare void @srand(i32)

define i32 @getNumber(){
   entry:
      %0 = call i32 @rand()
      %1 = srem i32 %0, 100
      ret i32 %1
}

    @.strR = private unnamed_addr constant [3 x i8] c"%d\00", align 1

; Function Attrs: nounwind uwtable
define i32 @readInt() #0 {
  %x = alloca i32, align 4
  %1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x)
  %2 = load i32, i32* %x, align 4
  ret i32 %2
}

@.str = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

define void @println(i32 %x) {
  %1 = alloca i32, align 4
  store i32 %x, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str, i32 0, i32 0), i32 %2)
  ret void
}

declare i32 @printf(i8*, ...)

declare i32 @__isoc99_scanf(i8*, ...) #1

; External declaration of the getchar function
declare i32 @getchar()

; External declaration of the putchar function
declare i32 @putchar(i32);

    define i32 @min(i32* %a, i32* %b) {
    
entry:
    ; Allocate memory for Imp variables
    %c = alloca i32
    %0 = load i32, i32* %a
    %1 = load i32, i32* %b
    ; Logical operation <=
    %2 = icmp sle i32 %0, %1
    br i1 %2, label %if.n1.true, label %if.n1.false
    
if.n1.true:
    ; Assignation of variable c
    %3 = load i32, i32* %a
    store i32 %3, i32* %c
    br label %if.n1
    
if.n1.false:
    ; Assignation of variable c
    %4 = load i32, i32* %b
    store i32 %4, i32* %c
    br label %if.n1
    
if.n1:
    %5 = load i32, i32* %c
    ret i32 %5
    ret i32 0
}


define i32 @max(i32* %a, i32* %b) {    
entry:
    ; Allocate memory for Imp variables
    %c = alloca i32
    %0 = load i32, i32* %a
    %1 = load i32, i32* %b
    ; Logical operation >=
    %2 = icmp sge i32 %0, %1
    br i1 %2, label %if.n2.true, label %if.n2.false
    
if.n2.true:
    ; Assignation of variable c
    %3 = load i32, i32* %a
    store i32 %3, i32* %c
    br label %if.n2
    
if.n2.false:
    ; Assignation of variable c
    %4 = load i32, i32* %b
    store i32 %4, i32* %c
    br label %if.n2
    
if.n2:
    %5 = load i32, i32* %c
    ret i32 %5
    ret i32 0
}

    define i32 @foo(i32* %a, i32* %k) {
    
entry:
    ; Allocate memory for Imp variables
    %b = alloca i32
    %i = alloca i32
    %j = alloca i32
    %c = alloca i32
    %x = alloca i32
    ; Assignation of variable x
    %0 = load i32, i32* %a
    %1 = load i32, i32* %k
    ; Arithmetic operation +
    %2 = add i32 %0, %1
    store i32 %2, i32* %x
    ; Print ( x ) 
    %3 = load i32, i32* %x
    call void @println(i32 %3)
    %4 = add i32 0, 0
    store i32 %4, i32* %i
    %5 = add i32 0, 5
    br label %for.n6.cond
    
for.n6.cond.body:
    ; Print ( a ) 
    %6 = load i32, i32* %a
    call void @println(i32 %6)
    ; Increment counter %i
    %7 = load i32, i32* %i
    %8 = add i32 %7, 1
    store i32 %8, i32* %i
    br label %for.n6.cond
    
for.n6.cond:
    %9 = load i32, i32* %i
    %10 = icmp ne i32 %9, %5
    br i1 %10, label %for.n6.cond.body, label %for.n6.cond.end
    
for.n6.cond.end:
    %11 = load i32, i32* %a
    ret i32 %11
    ret i32 0
    }
    

define i32 @main() {
entry:
    ; RNG initialization
    %0 = call i32 @time(i32* null)
    call void @srand(i32 %0)

    ; Allocate memory for Imp variables
    %a = alloca i32
    %b = alloca i32
    %i = alloca i32
    %j = alloca i32
    %c = alloca i32
    %k = alloca i32
    %x = alloca i32
    %tmp = alloca i32
    
    
    
    ; Read ( a ) 
    %1 = call i32 @readInt()
    store i32 %1, i32* %a
    
    
    ; Assignation of variable b
    %2 = add i32 0, 42
    store i32 %2, i32* %b
    
    
    ; Assignation of variable b
    %3 = load i32, i32* %a
    store i32 %3, i32* %b
    
    
    ; Assignation of variable b
    %4 = load i32, i32* %a
    %5 = add i32 0, 8
    ; Arithmetic operation +
    %6 = add i32 %4, %5
    store i32 %6, i32* %b
    
    
    ; Assignation of variable b
    %7 = add i32 0, 5
    %8 = load i32, i32* %a
    %9 = load i32, i32* %a
    %10 = add i32 0, 9
    ; Arithmetic operation /
    %11 = sdiv i32 %9, %10
    ; Arithmetic operation *
    %12 = mul i32 %8, %11
    ; Arithmetic operation -
    %13 = sub i32 %7, %12
    store i32 %13, i32* %b
    
    
    ; Print ( b ) 
    %14 = load i32, i32* %b
    call void @println(i32 %14)
    
    
    %15 = load i32, i32* %a
    %16 = load i32, i32* %b
    %17 = add i32 0, 100
    ; Arithmetic operation +
    %18 = add i32 %16, %17
    ; Logical operation <
    %19 = icmp slt i32 %15, %18
    br i1 %19, label %if.n1.true, label %if.n1.false
    
if.n1.true:
    ; Assignation of variable a
    %20 = add i32 0, 55
    store i32 %20, i32* %a
    
    br label %if.n1
    
if.n1.false:
    ; Assignation of variable a
    %21 = add i32 0, 44
    store i32 %21, i32* %a
    
    br label %if.n1
    
if.n1:
    
    
    br label %while.n2.cond
    
while.n2.cond:
    %22 = load i32, i32* %a
    %23 = add i32 0, 200
    ; Logical operation <
    %24 = icmp slt i32 %22, %23
    br i1 %24, label %while.n2.cond.body, label %while.n2.cond.end
    
while.n2.cond.body:
    ; Assignation of variable a
    %25 = load i32, i32* %a
    %26 = add i32 0, 1
    ; Arithmetic operation +
    %27 = add i32 %25, %26
    store i32 %27, i32* %a
    
    br label %while.n2.cond
    
while.n2.cond.end:
    
    
    ; Print ( a ) 
    %28 = load i32, i32* %a
    call void @println(i32 %28)
    
    
    %29 = add i32 0, 2
    store i32 %29, i32* %i
    %30 = add i32 0, 2
    %31 = add i32 0, 12
    br label %for.n3.cond
    
for.n3.cond.body:
    ; Print ( i ) 
    %32 = load i32, i32* %i
    call void @println(i32 %32)
    
    ; Increment counter %i
    %33 = load i32, i32* %i
    %34 = add i32 %33, %30
    store i32 %34, i32* %i
    br label %for.n3.cond
    
for.n3.cond:
    %35 = load i32, i32* %i
    %36 = icmp ne i32 %35, %31
    br i1 %36, label %for.n3.cond.body, label %for.n3.cond.end
    
for.n3.cond.end:
    
    
    %37 = add i32 0, 27
    store i32 %37, i32* %i
    %38 = add i32 0, 3
    %39 = sub i32 0, %38
    %40 = add i32 0, 3
    %41 = sub i32 0, %40
    br label %for.n4.cond
    
for.n4.cond.body:
    %42 = add i32 0, 0
    store i32 %42, i32* %j
    %43 = add i32 0, 2
    br label %for.n5.cond
    
for.n5.cond.body:
    ; Print ( i ) 
    %44 = load i32, i32* %i
    call void @println(i32 %44)
    
    ; Increment counter %j
    %45 = load i32, i32* %j
    %46 = add i32 %45, 1
    store i32 %46, i32* %j
    br label %for.n5.cond
    
for.n5.cond:
    %47 = load i32, i32* %j
    %48 = icmp ne i32 %47, %43
    br i1 %48, label %for.n5.cond.body, label %for.n5.cond.end
    
for.n5.cond.end:
    
    ; Increment counter %i
    %49 = load i32, i32* %i
    %50 = add i32 %49, %39
    store i32 %50, i32* %i
    br label %for.n4.cond
    
for.n4.cond:
    %51 = load i32, i32* %i
    %52 = icmp ne i32 %51, %41
    br i1 %52, label %for.n4.cond.body, label %for.n4.cond.end
    
for.n4.cond.end:
    
    
    ; Rand ( c ) 
    %53 = call i32 @rand()
    store i32 %53, i32* %c
    
    
    ; Print ( c ) 
    %54 = load i32, i32* %c
    call void @println(i32 %54)
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    ; Assignation of variable b
    %55 = load i32, i32* %c
    %56 = add i32 0, 1
    ; Arithmetic operation +
    %57 = add i32 %55, %56
    store i32 %57, i32* %b
    
    
    %58 = load i32, i32* %b
    %59 = load i32, i32* %c
    %c0c0 = alloca i32
    store i32 %58, i32* %c0c0
    %c0c1 = alloca i32
    store i32 %59, i32* %c0c1
    %60 = call i32 @foo(i32* %c0c0, i32* %c0c1)
    
    
    ; Assignation of variable b
    %61 = add i32 0, 7
    %62 = add i32 0, 5
    ; Arithmetic operation *
    %63 = mul i32 %61, %62
    %64 = add i32 0, 8
    %c1c0 = alloca i32
    store i32 %63, i32* %c1c0
    %c1c1 = alloca i32
    store i32 %64, i32* %c1c1
    %65 = call i32 @min(i32* %c1c0, i32* %c1c1)
    store i32 %65, i32* %b
    
    
    ; Print ( b ) 
    %66 = load i32, i32* %b
    call void @println(i32 %66)
    
    
    ; Assignation of variable b
    %67 = add i32 0, 7
    %68 = add i32 0, 5
    ; Arithmetic operation *
    %69 = mul i32 %67, %68
    %70 = add i32 0, 8
    %c2c0 = alloca i32
    store i32 %69, i32* %c2c0
    %c2c1 = alloca i32
    store i32 %70, i32* %c2c1
    %71 = call i32 @max(i32* %c2c0, i32* %c2c1)
    store i32 %71, i32* %b
    
    
    ; Print ( b ) 
    %72 = load i32, i32* %b
    call void @println(i32 %72)
    
    

    ret i32 0    
}

