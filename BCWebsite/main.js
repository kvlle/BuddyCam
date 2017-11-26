function changeRes(){
  var x = document.getElementById('non_rep');
    if (x.className === "nav_container") {
      x.className += "_responsive";
    } else {
      x.className = "nav_container"
    }
}
