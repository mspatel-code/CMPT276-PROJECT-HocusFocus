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
