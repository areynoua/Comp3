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

    ; Allocate memory for Imp variables
    %a = alloca i32
    %b = alloca i32
    
    ; Read ( a ) 
    %2 = call i32 @readInt()
    store i32 %2, i32* %a
    
    ; Print ( a ) 
    %3 = load i32, i32* %a
    call void @println(i32 %3)
    
    ; Read ( a ) 
    %4 = call i32 @readInt()
    store i32 %4, i32* %a
    
    ; b := stuff
    %5 = add i32 0, 42
    store i32 %5, i32* %b
    

    ret i32 0    
}

