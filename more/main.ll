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

    

define i32 @main() {
entry:
    ; RNG initialization
    %0 = call i32 @time(i32* null)
    call void @srand(i32 %0)

    ; Allocate memory for Imp variables
    %a = alloca i32
    %b = alloca i32
    %c = alloca i32
    %tmp = alloca i32
    
    ; Read ( a ) 
    %1 = call i32 @readInt()
    store i32 %1, i32* %a
    
    
    ; Read ( b ) 
    %2 = call i32 @readInt()
    store i32 %2, i32* %b
    
    
    br label %while.n1.cond
    
while.n1.cond:
    %3 = load i32, i32* %b
    %4 = add i32 0, 0
    ; Logical operation <>
    %5 = icmp ne i32 %3, %4
    br i1 %5, label %while.n1.cond.body, label %while.n1.cond.end
    
while.n1.cond.body:
    ; Assignation of variable c
    %6 = load i32, i32* %b
    store i32 %6, i32* %c
    
    
    br label %while.n2.cond
    
while.n2.cond:
    %7 = load i32, i32* %a
    %8 = load i32, i32* %b
    ; Logical operation >=
    %9 = icmp sge i32 %7, %8
    br i1 %9, label %while.n2.cond.body, label %while.n2.cond.end
    
while.n2.cond.body:
    ; Assignation of variable a
    %10 = load i32, i32* %a
    %11 = load i32, i32* %b
    ; Arithmetic operation -
    %12 = sub i32 %10, %11
    store i32 %12, i32* %a
    
    br label %while.n2.cond
    
while.n2.cond.end:
    
    
    ; Assignation of variable b
    %13 = load i32, i32* %a
    store i32 %13, i32* %b
    
    
    ; Assignation of variable a
    %14 = load i32, i32* %c
    store i32 %14, i32* %a
    
    br label %while.n1.cond
    
while.n1.cond.end:
    
    
    ; Print ( a ) 
    %15 = load i32, i32* %a
    call void @println(i32 %15)
    
    

    ret i32 0    
}

