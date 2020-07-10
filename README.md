This project is for a graphic language called 'Layan' (Under Development).
- project name: 'Layan'.
- name asset: arabic language.
- tools: java
- start date: 18/8/2019
- goal: with layan you can draw and write on a 2d plane with a simple syntax.

# Hot to use layan
### Variables declration.
## There is a four data type in this language (string, int, float, bool).

```c#
string name = "layan";
int year = 2019;
bool isComplete = false;
```

### Control flow :

```c#
if(isComplete){
  print("Layan is a great language");
 }else{
  print("Layan also is a great language");
 }
```

```c#
while(true){
  /* Draw a circle at random position in the canvas */
  Circle myCircle(RAND * WIDTH, RAND * WIDTH, RAND * 10);
  myCircle.draw();
}
```
```c#
for(int i = 0; i <= 10; i = i + 1){
  Square mySquare(50, 50, i, i);
  mySquare.draw();
  wait(50);
}
```

## Built-in drawable classes:
### Layan has a built-in drawable class to draw a primitive object such as circle and square.

* Circle(x, y, radius);
* Square(x, y, halfWidth);
* Rectangle(x, y, halfWidth, halfHeight);
* Point(x, y);
* FilledCircle(x, y, radius);
* FilledSquare(x, y, halfWidth);
* FilledRectangle(x, y, halfWidth, halfHeight);
* Line(a, b, c, d);
* Text(x, y, value);

### use the draw function to draw the object.

### Examples:

```c#
Circle(20, 30, 5) myCircle;
myCircle.draw();
```

```c#
setPenSize(0.05);
setScale(0 , 100);
setColor("blue");

while(true){
    FilledCircle myCircle(RAND * 100, RAND * 100, RAND * 10);
    myCircle.draw();
    setRandomColor();
}
```

## Function declration in Layan:

```c#
setPenSize(0.005);
setScale(0 , 100);
setColor("blue");

function CircleSquare(float xPosition, float yPosition, float halfWidth){
  Square square(xPosition, yPosition, halfWidth);
  square.draw();

  Circle circle(xPosition, yPosition, halfWidth);
  circle.draw();
}

CircleSquare(50, 50, 10);
```

