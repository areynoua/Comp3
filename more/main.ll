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
    ; RNG initialization
    %1 = call i32 @time(i32* null)
    call void @srand(i32 %1)

    
entry:
    ; Allocate memory for Imp variables
    %a = alloca i32
    %b = alloca i32
    
    ; Read ( a ) 
    %2 = call i32 @readInt()
    store i32 %2, i32* %a
    
    ; Print ( a ) 
    %3 = load i32, i32* %a
    call void @println(i32 %3)
    
    ; b := stuff
    %4 = add i32 0, 42
    store i32 %4, i32* %b
    
    ; a := stuff
    %5 = add i32 0, 5
    %6 = sub i32 0, %5
    store i32 %6, i32* %a
    
    ; b := stuff
    %7 = load i32, i32* a
    store i32 %7, i32* %b
    
    ; b := stuff
    %8 = load i32, i32* a
    %9 = add i32 0, 8
    %10 = add i32 %8, %9
    store i32 %10, i32* %b
    
    ; b := stuff
    %11 = add i32 0, 5
    %12 = load i32, i32* a
    %13 = load i32, i32* a
    %14 = add i32 0, 9
    %15 = sdiv i32 %13, %14
    %16 = mul i32 %12, %15
    %17 = sub i32 %11, %16
    store i32 %17, i32* %b
    
    %18 = load i32, i32* a
    %19 = load i32, i32* b
    %20 = add i32 0, 100
    %21 = add i32 %19, %20
    %22 = icmp slt i32 %18, %21
    br i1 %22, label label1t, label label1f
    
label1t:
    ; a := stuff
    %23 = add i32 0, 55
    store i32 %23, i32* %a
    br label label1
    
label1f:
    ; a := stuff
    %24 = add i32 0, 44
    store i32 %24, i32* %a
    br label label1
    
label1:
    
    

    ret i32 0    
}

