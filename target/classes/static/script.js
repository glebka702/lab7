const API_BASE = "/api";

function loadCategories() {
    fetch(`${API_BASE}/categories`)
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("categoryList");
            list.innerHTML = "";
            data.forEach(cat => {
                const li = document.createElement("li");
                li.textContent = `${cat.id}: ${cat.name}`;
                list.appendChild(li);
            });
        });
}

function searchAndSave() {
    const term = document.getElementById("wikiTerm").value;
    const categoryId = document.getElementById("categoryId").value;
    fetch(`${API_BASE}/wiki/search-and-save?term=${encodeURIComponent(term)}&categoryId=${categoryId}`, {
        method: "POST"
    })
        .then(res => res.json())
        .then(data => alert(`Сохранено: ${data.term}`))
        .catch(err => alert("Ошибка: " + err));
}

function loadWikiByCategory() {
    const categoryId = document.getElementById("searchCategoryId").value;
    fetch(`${API_BASE}/wiki/by-category/${categoryId}`)
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("wikiList");
            list.innerHTML = "";
            data.forEach(wp => {
                const li = document.createElement("li");
                li.textContent = `${wp.term}: ${wp.summary}`;
                list.appendChild(li);
            });
        });
}
