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

define i32 @main() {
entry:
    ; RNG initialization
    %0 = call i32 @time(i32* null)
    call void @srand(i32 %0)

    ; Allocate memory for Imp variables
    %a = alloca i32
    %b = alloca i32
    
    ; Read ( a ) 
    %1 = call i32 @readInt()
    store i32 %1, i32* %a
    
    ; b := stuff
    %2 = add i32 0, 42
    store i32 %2, i32* %b
    
    ; b := stuff
    %3 = load i32, i32* %a
    store i32 %3, i32* %b
    
    ; b := stuff
    %4 = load i32, i32* %a
    %5 = add i32 0, 8
    %6 = add i32 %4, %5
    store i32 %6, i32* %b
    
    ; b := stuff
    %7 = add i32 0, 5
    %8 = load i32, i32* %a
    %9 = load i32, i32* %a
    %10 = add i32 0, 9
    %11 = sdiv i32 %9, %10
    %12 = mul i32 %8, %11
    %13 = sub i32 %7, %12
    store i32 %13, i32* %b
    
    ; Print ( b ) 
    %14 = load i32, i32* %b
    call void @println(i32 %14)
    
    %15 = load i32, i32* %a
    %16 = load i32, i32* %b
    %17 = add i32 0, 100
    %18 = add i32 %16, %17
    %19 = icmp slt i32 %15, %18
    br i1 %19, label %if.n1.true, label %if.n1.false
    
if.n1.true:
    ; a := stuff
    %20 = add i32 0, 55
    store i32 %20, i32* %a
    br label %if.n1
    
if.n1.false:
    ; a := stuff
    %21 = add i32 0, 44
    store i32 %21, i32* %a
    br label %if.n1
    
if.n1:
    
    
    br label %while.n2.cond
    
while.n2.cond:
    %22 = load i32, i32* %a
    %23 = add i32 0, 200
    %24 = icmp slt i32 %22, %23
    br i1 %24, label %while.n2.cond.body, label %while.n2.cond.end
    
while.n2.cond.body:
    ; a := stuff
    %25 = load i32, i32* %a
    %26 = add i32 0, 1
    %27 = add i32 %25, %26
    store i32 %27, i32* %a
    br label %while.n2.cond
    
while.n2.cond.end:
    
    
    ; Print ( a ) 
    %28 = load i32, i32* %a
    call void @println(i32 %28)
    

    ret i32 0    
}

