var start = document.getElementById('start');
var stop = document.getElementById('stop');
var reset = document.getElementById('reset');


var workDuration = document.getElementById('workTime');
var breakDuration = document.getElementById('breakTime');

var min = document.getElementById('minutes');
var sec = document.getElementById('seconds');

var stats = document.getElementById('condition');
stats.innerText = "Work";

//set workDuration or breakDuration on clock
min.innerText = workDuration.value;

//set work Duration of clock on change
workDuration.addEventListener("change", changeWorkDuration);
function changeWorkDuration(){
    if(workDuration.value <  1){
        alert("Please input a duration of more than 1 minute")
        workDuration.value = 25;
    }else{
        min.innerText = workDuration.value;
    }
}

breakDuration.addEventListener("change", changeBreakDuration)
function changeBreakDuration(){
    if(breakDuration.value < 1){
        alert("Please input a duration of more than 1 minute")
        breakDuration.value = 5;
    }
}


//store a reference to a timer variable
var startTimer; 

start.addEventListener('click', function(){
    if(startTimer === undefined){
        startTimer = setInterval(timer, 1000);
        start.disabled = true;
        workDuration.disabled = true;
        breakDuration.disabled = true;
    } else{
        alert("time is already running!");
    }
})

reset.addEventListener('click', function(){
    workDuration.disabled = false;
    breakDuration.disabled = false;
    min.innerText = workDuration.value;
    sec.innerText = "00";

    stopInterval();
    startTimer = undefined;
})

stop.addEventListener('click', function(){
    workDuration.disabled = true
    breakDuration.disabled = true;
    stopInterval();
    startTimer = undefined;
})

var audio = new Audio("https://www.myinstants.com/media/sounds/alarm_clock.mp3");

//start timer function
function timer(){

    //work timer countdown
    if(sec.innerText != 0){
        sec.innerText--;
    } else if(min.innerText != 0 && sec.innerText == 0){
        sec.innerText = 59;
        min.innerText--;
    }

    //break timer countdown 
    if(min.innerText == 0 && sec.innerText == 0 && stats.innerText == "Work"){
        audio.play();
        stopInterval();
        stats.innerText = "Break";
        min.innerText = breakDuration.value;
        sec.innerText = "00";
    }

    //increment counter by one if one full cycle is completed 
    if(min.innerText == 0 && sec.innerText == 0 && stats.innerText == "Break" ){
        audio.play();
        stopInterval();
        stats.innerText = "Work";
        min.innerText = workDuration.value;
        sec.innerText = "00";

        $.ajax('/time', {
            type: 'POST',  // http methods
            data: workDuration.value,  // data to submit
            success: function(){
                console.log(workDuration.value);
            }
        })
    }

}

//stop timer function
function stopInterval(){
    start.disabled = false;
    clearInterval(startTimer);
    startTimer = undefined;
}

/* ================================================================================================================================ */
/* Moin's Todo list */

//Selectors
const todoInput = document.querySelector('.todo-input'); 
const todoButton = document.querySelector('.todo-button'); 
const todoList = document.querySelector('.todo-list'); 
const filterOption = document.querySelector('.filter-todo');
//Listeners

todoButton.addEventListener('click', addTodo);
todoList.addEventListener('click', deleteCheck);
filterOption.addEventListener('click', filterTodo);
document.addEventListener('DOMContentLoaded', getTodos);

//Functions

function addTodo(event)
{
//Prevent form from submitting
event.preventDefault();

//Todo Div
const todoDiv = document.createElement('div');
todoDiv.classList.add("todo");

//Create LI
const newTodo = document.createElement('li');
newTodo.innerText = todoInput.value;
newTodo.classList.add('todo-item');
newTodo.classList.add('text-start');
todoDiv.appendChild(newTodo); 
//ADD Todo to local storage
saveLocalTodos(todoInput.value);
//CHECK MARK BUTTON
const completedButton = document.createElement('button');
completedButton.innerHTML = '<i class = "fas fa-check"></i>';
completedButton.classList.add("complete-btn");
todoDiv.appendChild(completedButton);

//CHECK TRASH BUTTON
const trashButton = document.createElement('button');
trashButton.innerHTML = '<i class = "fas fa-trash "></i>';
trashButton.classList.add("trash-btn");
todoDiv.appendChild(trashButton);

//APPEND TO LIST
todoList.appendChild(todoDiv);

//Clear Todo Input Value
todoInput.value = "";
}

function deleteCheck(e)
{
    const item = e.target;
    //DELETE TODO
    if(item.classList[0] ==="trash-btn")
    {
        const todo = item.parentElement;
        removeLocalTodos(todo);
        todo.remove();
    }
    // CHECK MARK
    if(item.classList[0] === "complete-btn")
    {
        const todo = item.parentElement;
        todo.classList.toggle('completed');
    }
}

function filterTodo(e)
{
    const todos = todoList.childNodes;
    todos.forEach(function(todo)
    {
        switch(e.target.value){
        case "all":
            todo.style.display = "flex";
            break;
        
        case "completed":
        if(todo.classList.contains('completed')){
            todo.style.display = "flex";
        }
        else 
        {
            todo.style.display = "none";
        }
        break;
        case "uncompleted":
        if(!todo.classList.contains('completed')){
            todo.style.display = "flex";
        }
        else 
        {
            todo.style.display = "none";
        }
        break;
    }
});
}

function saveLocalTodos(todo)
{
    let todos;
    if(localStorage.getItem("todos") ===  null)
    {
        todos = [];
    }
    else
    {
        todos = JSON.parse(localStorage.getItem("todos"));  
    }    
    todos.push(todo);
    localStorage.setItem("todos", JSON.stringify(todos));
}

function  getTodos()
{
    let todos;
    if(localStorage.getItem("todos") ===  null)
    {
        todos = [];
    }
    else
    {
        todos = JSON.parse(localStorage.getItem("todos"));  
    }    
todos.forEach(function(todo)
{
//Todo Div
const todoDiv = document.createElement('div');
todoDiv.classList.add("todo");

//Create LI
const newTodo = document.createElement('li');
newTodo.innerText = todo;
newTodo.classList.add('todo-item');
newTodo.classList.add('text-start');
todoDiv.appendChild(newTodo); 
//CHECK MARK BUTTON
const completedButton = document.createElement('button');
completedButton.innerHTML = '<i class = "fas fa-check"></i>';
completedButton.classList.add("complete-btn");
todoDiv.appendChild(completedButton);

//CHECK TRASH BUTTON
const trashButton = document.createElement('button');
trashButton.innerHTML = '<i class = "fas fa-trash "></i>';
trashButton.classList.add("trash-btn");
todoDiv.appendChild(trashButton);

//APPEND TO LIST
todoList.appendChild(todoDiv);   
    });
}

function removeLocalTodos(todo)
{
    let todos;
    if(localStorage.getItem("todos") ===  null)
    {
        todos = [];
    }
    else
    {
        todos = JSON.parse(localStorage.getItem("todos"));  
    }     

    const todoIndex = todo.children[0].innerText;
    todos.splice(todos.indexOf(todoIndex), 1);
    localStorage.setItem("todos",JSON.stringify(todos));
}


//Quotes API
let url = "https://type.fit/api/quotes"


function fetchData() {
  fetch(url).then(response => {
    return response.json();
  }).then(data => {
    console.log("I am called")
    let i = Math.floor(Math.random() * 1644)
    author = data[i].author;
    quote = data[i].text;
    document.getElementById("quote").innerHTML = quote
    document.getElementById("author").innerHTML = author
  });
}

fetchData()
setInterval(fetchData, 120000);