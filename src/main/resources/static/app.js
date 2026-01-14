// API Base URL
const API_BASE = 'http://localhost:8080/api';

// Global state
let kategorijos = [];
let parduotuves = [];
let darbuotojai = [];
let pareigos = [];
let prekes = [];
let currentCart = [];

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    initializeNavigation();
    loadInitialData();
    loadDashboard();
});

// Navigation
function initializeNavigation() {
    document.querySelectorAll('[data-page]').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.getAttribute('data-page');
            showPage(page);
            
            // Update active nav link
            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });
}

function showPage(page) {
    document.querySelectorAll('.page-content').forEach(p => p.classList.remove('active'));
    document.getElementById(`${page}-page`).classList.add('active');
    
    // Load page data
    switch(page) {
        case 'dashboard': loadDashboard(); break;
        case 'prekes': loadPrekes(); break;
        case 'inventorius': loadInventorius(); break;
        case 'pardavimai': loadPardavimai(); break;
        case 'parduotuves': loadParduotuves(); break;
        case 'darbuotojai': loadDarbuotojai(); break;
        case 'pareigos': loadPareigos(); break;
        case 'pardavimo-eilutes': loadPardavimoEilutes(); break;
    }
}

// Load initial reference data
async function loadInitialData() {
    try {
        [kategorijos, parduotuves, pareigos] = await Promise.all([
            fetch(`${API_BASE}/kategorijos`).then(r => r.json()),
            fetch(`${API_BASE}/parduotuves`).then(r => r.json()),
            fetch(`${API_BASE}/pareigos`).then(r => r.json())
        ]);
        
        // Populate filters
        populateKategorijosFilter();
        populateParduotuvesFilter();
    } catch (error) {
        console.error('Klaida kraunant duomenis:', error);
        showError('Nepavyko užkrauti pradinių duomenų');
    }
}

function populateKategorijosFilter() {
    const select = document.getElementById('filter-kategorija');
    select.innerHTML = '<option value="">Visos kategorijos</option>';
    kategorijos.forEach(k => {
        select.innerHTML += `<option value="${k.kategorijosId}">${k.pavadinimas}</option>`;
    });
}

function populateParduotuvesFilter() {
    const selects = ['filter-parduotuve', 'filter-pardavimas-parduotuve', 'filter-darbuotojas-parduotuve'];
    selects.forEach(id => {
        const select = document.getElementById(id);
        if (select) {
            select.innerHTML = '<option value="">Visos parduotuvės</option>';
            parduotuves.forEach(p => {
                select.innerHTML += `<option value="${p.id}">${p.miestas} - ${p.gatve}</option>`;
            });
        }
    });
}

// Dashboard
async function loadDashboard() {
    try {
        const [prekesData, parduotuvesData, darbuotojaiData, inventoriusData, pardavimaiData] = await Promise.all([
            fetch(`${API_BASE}/prekes`).then(r => r.json()),
            fetch(`${API_BASE}/parduotuves`).then(r => r.json()),
            fetch(`${API_BASE}/darbuotojai`).then(r => r.json()),
            fetch(`${API_BASE}/inventorius`).then(r => r.json()),
            fetch(`${API_BASE}/pardavimai`).then(r => r.json())
        ]);
        
        document.getElementById('total-prekes').textContent = prekesData.length;
        document.getElementById('total-parduotuves').textContent = parduotuvesData.length;
        document.getElementById('total-darbuotojai').textContent = darbuotojaiData.length;
        
        // Today's sales
        const today = new Date().toISOString().split('T')[0];
        const todaySales = pardavimaiData.filter(p => p.dataLaikas.startsWith(today));
        document.getElementById('total-pardavimai').textContent = todaySales.length;
        
        // Low stock
        const lowStock = inventoriusData.filter(i => i.minimalusKiekis && i.kiekis <= i.minimalusKiekis);
        displayLowStock(lowStock.slice(0, 5));
        
        // Recent sales
        displayRecentSales(pardavimaiData.slice(0, 5));
    } catch (error) {
        console.error('Klaida kraunant dashboard:', error);
    }
}

function displayLowStock(items) {
    const tbody = document.getElementById('low-stock-table');
    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Nėra žemų atsargų</td></tr>';
        return;
    }
    
    tbody.innerHTML = items.map(item => `
        <tr>
            <td>${item.prekesId}</td>
            <td>${item.parduotuvesId}</td>
            <td><span class="status-low">${item.kiekis}</span> / ${item.minimalusKiekis}</td>
        </tr>
    `).join('');
}

function displayRecentSales(sales) {
    const tbody = document.getElementById('recent-sales-table');
    if (sales.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Nėra pardavimų</td></tr>';
        return;
    }
    
    tbody.innerHTML = sales.map(sale => `
        <tr>
            <td>${formatDateTime(sale.dataLaikas)}</td>
            <td>${sale.parduotuvesId}</td>
            <td><strong>${formatPrice(sale.bendraSuma)}</strong></td>
        </tr>
    `).join('');
}

// Prekės
async function loadPrekes() {
    try {
        prekes = await fetch(`${API_BASE}/prekes`).then(r => r.json());
        displayPrekes(prekes);
        
        // Setup search and filter
        document.getElementById('search-preke').addEventListener('input', filterPrekes);
        document.getElementById('filter-kategorija').addEventListener('change', filterPrekes);
    } catch (error) {
        console.error('Klaida kraunant prekes:', error);
        showError('Nepavyko užkrauti prekių');
    }
}

function displayPrekes(items) {
    const tbody = document.getElementById('prekes-table');
    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">Prekių nerasta</td></tr>';
        return;
    }
    
    tbody.innerHTML = items.map(preke => {
        const kategorija = kategorijos.find(k => k.kategorijosId === preke.kategorijosId);
        return `
            <tr>
                <td>${preke.prekesId}</td>
                <td><strong>${preke.pavadinimas}</strong></td>
                <td>${preke.bruksninisKodas || '-'}</td>
                <td>${kategorija ? kategorija.pavadinimas : '-'}</td>
                <td>${formatPrice(preke.pirkimoKaina)}</td>
                <td>${formatPrice(preke.pardavimoKaina)}</td>
                <td>${preke.matoVienetas || '-'}</td>
                <td>
                    <button class="btn btn-sm btn-info" onclick="editPreke(${preke.prekesId})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deletePreke(${preke.prekesId})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

function filterPrekes() {
    const search = document.getElementById('search-preke').value.toLowerCase();
    const kategorijaId = document.getElementById('filter-kategorija').value;
    
    let filtered = prekes;
    
    if (search) {
        filtered = filtered.filter(p => 
            p.pavadinimas.toLowerCase().includes(search) || 
            (p.bruksninisKodas && p.bruksninisKodas.toLowerCase().includes(search))
        );
    }
    
    if (kategorijaId) {
        filtered = filtered.filter(p => p.kategorijosId == kategorijaId);
    }
    
    displayPrekes(filtered);
}

function showPrekeModal(prekeId = null) {
    const isEdit = prekeId !== null;
    const preke = isEdit ? prekes.find(p => p.prekesId === prekeId) : {};
    
    const modal = `
        <div class="modal fade" id="prekeModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${isEdit ? 'Redaguoti prekę' : 'Nauja prekė'}</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="prekeForm">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Pavadinimas *</label>
                                    <input type="text" class="form-control" name="pavadinimas" value="${preke.pavadinimas || ''}" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Brūkšninis kodas</label>
                                    <input type="text" class="form-control" name="bruksninisKodas" value="${preke.bruksninisKodas || ''}">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Aprašymas</label>
                                <textarea class="form-control" name="aprasymas" rows="2">${preke.aprasymas || ''}</textarea>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Kategorija</label>
                                    <select class="form-select" name="kategorijosId">
                                        <option value="">Pasirinkite...</option>
                                        ${kategorijos.map(k => `
                                            <option value="${k.kategorijosId}" ${preke.kategorijosId === k.kategorijosId ? 'selected' : ''}>
                                                ${k.pavadinimas}
                                            </option>
                                        `).join('')}
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Mato vienetas</label>
                                    <select class="form-select" name="matoVienetas">
                                        <option value="vnt" ${preke.matoVienetas === 'vnt' ? 'selected' : ''}>vnt</option>
                                        <option value="kg" ${preke.matoVienetas === 'kg' ? 'selected' : ''}>kg</option>
                                    </select>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Pirkimo kaina (€)</label>
                                    <input type="number" step="0.01" class="form-control" name="pirkimoKaina" value="${preke.pirkimoKaina || ''}">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Pardavimo kaina (€) *</label>
                                    <input type="number" step="0.01" class="form-control" name="pardavimoKaina" value="${preke.pardavimoKaina || ''}" required>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Atšaukti</button>
                        <button type="button" class="btn btn-primary" onclick="savePreke(${prekeId})">Išsaugoti</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    showModal(modal);
}

async function savePreke(prekeId) {
    const form = document.getElementById('prekeForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const formData = new FormData(form);
    const data = {
        pavadinimas: formData.get('pavadinimas'),
        aprasymas: formData.get('aprasymas'),
        bruksninisKodas: formData.get('bruksninisKodas'),
        pirkimoKaina: parseFloat(formData.get('pirkimoKaina')) || null,
        pardavimoKaina: parseFloat(formData.get('pardavimoKaina')),
        matoVienetas: formData.get('matoVienetas'),
        kategorijosId: parseInt(formData.get('kategorijosId')) || null
    };
    
    try {
        const url = prekeId ? `${API_BASE}/prekes/${prekeId}` : `${API_BASE}/prekes`;
        const method = prekeId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            closeModal();
            showSuccess(prekeId ? 'Prekė atnaujinta' : 'Prekė sukurta');
            loadPrekes();
        } else {
            const errorText = await response.text();
            showError(`Nepavyko išsaugoti prekės: ${errorText || 'nežinoma klaida'}`);
        }
    } catch (error) {
        console.error('Klaida:', error);
        showError('Nepavyko išsaugoti prekės');
    }
}

function editPreke(prekeId) {
    showPrekeModal(prekeId);
}

async function deletePreke(prekeId) {
    if (!confirm('Ar tikrai norite ištrinti šią prekę?')) return;
    
    try {
        const response = await fetch(`${API_BASE}/prekes/${prekeId}`, { method: 'DELETE' });
        if (response.ok) {
            showSuccess('Prekė ištrinta');
            loadPrekes();
        } else {
            const errorText = await response.text();
            if (response.status === 409 || errorText.includes('constraint')) {
                showError('Prekė negali būti ištrinta - ji naudojama inventoriuje arba pardavimo eilutėse');
            } else {
                showError('Klaida trinant prekę: ' + errorText);
            }
        }
    } catch (error) {
        console.error('Klaida:', error);
        showError('Nepavyko ištrinti prekės');
    }
}

// Inventorius
async function loadInventorius() {
    try {
        const inventorius = await fetch(`${API_BASE}/inventorius`).then(r => r.json());
        displayInventorius(inventorius);
        
        document.getElementById('filter-parduotuve').addEventListener('change', () => displayInventorius(inventorius));
        document.getElementById('show-low-stock').addEventListener('change', () => displayInventorius(inventorius));
    } catch (error) {
        console.error('Klaida kraunant inventorių:', error);
        showError('Nepavyko užkrauti inventoriaus');
    }
}

function displayInventorius(items) {
    const parduotuveId = document.getElementById('filter-parduotuve').value;
    const showLowStock = document.getElementById('show-low-stock').checked;
    
    let filtered = items;
    
    if (parduotuveId) {
        filtered = filtered.filter(i => i.parduotuvesId == parduotuveId);
    }
    
    if (showLowStock) {
        filtered = filtered.filter(i => i.minimalusKiekis && i.kiekis <= i.minimalusKiekis);
    }
    
    const tbody = document.getElementById('inventorius-table');
    if (filtered.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">Įrašų nerasta</td></tr>';
        return;
    }
    
    tbody.innerHTML = filtered.map(item => {
        const status = item.minimalusKiekis && item.kiekis <= item.minimalusKiekis ? 'status-low' : 'status-ok';
        return `
            <tr>
                <td>${item.inventoriausId}</td>
                <td>${item.prekesId}</td>
                <td>${item.parduotuvesId}</td>
                <td class="${status}"><strong>${item.kiekis}</strong></td>
                <td>${item.minimalusKiekis || '-'}</td>
                <td>
                    ${item.minimalusKiekis && item.kiekis <= item.minimalusKiekis 
                        ? '<span class="badge bg-danger">Žemas</span>' 
                        : '<span class="badge bg-success">OK</span>'}
                </td>
                <td>${formatDateTime(item.paskutinisAtnaujinimas)}</td>
                <td>
                    <button class="btn btn-sm btn-info" onclick="editInventorius(${item.inventoriausId})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteInventorius(${item.inventoriausId})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

async function showInventoriusModal(inventoriausId = null) {
    const isEdit = inventoriausId !== null;
    let item = {};
    
    if (isEdit) {
        try {
            const response = await fetch(`${API_BASE}/inventorius/${inventoriausId}`);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }
            item = await response.json();
        } catch (error) {
            console.error('Klaida kraunant inventoriaus duomenis:', error);
            showError(`Nepavyko užkrauti inventoriaus duomenų: ${error.message}`);
            return;
        }
    }
    
    // Load prekės if not loaded
    if (prekes.length === 0) {
        prekes = await fetch(`${API_BASE}/prekes`).then(r => r.json());
    }
    
    const modal = `
        <div class="modal fade" id="inventoriusModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${isEdit ? 'Redaguoti inventorių' : 'Naujas inventoriaus įrašas'}</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="inventoriusForm">
                            <div class="mb-3">
                                <label class="form-label">Prekė *</label>
                                <select class="form-select" name="prekesId" required ${isEdit ? 'disabled' : ''}>
                                    <option value="">Pasirinkite...</option>
                                    ${prekes.map(p => `
                                        <option value="${p.prekesId}" ${item.prekesId === p.prekesId ? 'selected' : ''}>
                                            ${p.pavadinimas}
                                        </option>
                                    `).join('')}
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Parduotuvė *</label>
                                <select class="form-select" name="parduotuvesId" required ${isEdit ? 'disabled' : ''}>
                                    <option value="">Pasirinkite...</option>
                                    ${parduotuves.map(p => `
                                        <option value="${p.id}" ${item.parduotuvesId === p.id ? 'selected' : ''}>
                                            ${p.miestas} - ${p.gatve}
                                        </option>
                                    `).join('')}
                                </select>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Kiekis *</label>
                                    <input type="number" class="form-control" name="kiekis" value="${item.kiekis || 0}" required min="0">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Minimalus kiekis</label>
                                    <input type="number" class="form-control" name="minimalusKiekis" value="${item.minimalusKiekis || ''}" min="0">
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Atšaukti</button>
                        <button type="button" class="btn btn-primary" onclick="saveInventorius(${inventoriausId})">Išsaugoti</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    showModal(modal);
}

async function saveInventorius(inventoriausId) {
    const form = document.getElementById('inventoriusForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const formData = new FormData(form);
    const data = {
        prekesId: parseInt(formData.get('prekesId')),
        parduotuvesId: parseInt(formData.get('parduotuvesId')),
        kiekis: parseInt(formData.get('kiekis')),
        minimalusKiekis: formData.get('minimalusKiekis') ? parseInt(formData.get('minimalusKiekis')) : null
    };
    
    try {
        const url = inventoriausId ? `${API_BASE}/inventorius/${inventoriausId}` : `${API_BASE}/inventorius`;
        const method = inventoriausId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            closeModal();
            showSuccess(inventoriausId ? 'Inventorius atnaujintas' : 'Inventorius sukurtas');
            loadInventorius();
        } else {
            const errorText = await response.text();
            showError(`Nepavyko išsaugoti inventoriaus: ${errorText || 'nežinoma klaida'}`);
        }
    } catch (error) {
        console.error('Klaida:', error);
        showError('Nepavyko išsaugoti inventoriaus');
    }
}

function editInventorius(inventoriausId) {
    showInventoriusModal(inventoriausId);
}

async function deleteInventorius(inventoriausId) {
    if (!confirm('Ar tikrai norite ištrinti šį inventoriaus įrašą?')) return;
    
    try {
        const response = await fetch(`${API_BASE}/inventorius/${inventoriausId}`, { method: 'DELETE' });
        if (response.ok) {
            showSuccess('Inventorius ištrintas');
            loadInventorius();
        } else {
            const errorText = await response.text();
            if (response.status === 409 || errorText.includes('constraint')) {
                showError('Inventoriaus įrašas negali būti ištrintas - jis yra naudojamas sistemoje');
            } else {
                showError(`Nepavyko ištrinti inventoriaus: ${errorText || 'nežinoma klaida'}`);
            }
        }
    } catch (error) {
        console.error('Klaida trinant inventorių:', error);
        showError('Ryšio klaida - nepavyko ištrinti inventoriaus');
    }
}

// Pardavimai
async function loadPardavimai() {
    try {
        const pardavimai = await fetch(`${API_BASE}/pardavimai`).then(r => r.json());
        displayPardavimai(pardavimai);
    } catch (error) {
        console.error('Klaida kraunant pardavimus:', error);
        showError('Nepavyko užkrauti pardavimų');
    }
}

function displayPardavimai(items) {
    const tbody = document.getElementById('pardavimai-table');
    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">Pardavimų nerasta</td></tr>';
        return;
    }
    
    tbody.innerHTML = items.map(pardavimas => {
        const parduotuve = parduotuves.find(p => p.id === pardavimas.parduotuvesId);
        return `
            <tr>
                <td>${pardavimas.pardavimoId}</td>
                <td>${formatDateTime(pardavimas.dataLaikas)}</td>
                <td>${parduotuve ? `${parduotuve.miestas} - ${parduotuve.gatve}` : pardavimas.parduotuvesId}</td>
                <td>${pardavimas.darbuotojoId}</td>
                <td><strong>${formatPrice(pardavimas.bendraSuma)}</strong></td>
                <td>
                    <span class="badge bg-${pardavimas.busena === 'uzbaigtas' ? 'success' : 'warning'}">
                        ${pardavimas.busena || 'Naujas'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-info" onclick="viewPardavimas(${pardavimas.pardavimoId})">
                        <i class="bi bi-eye"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

function showPardavimasModal() {
    const modal = `
        <div class="modal fade" id="pardavimasModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Naujas pardavimas</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="pardavimasForm">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Parduotuvė *</label>
                                    <select class="form-select" name="parduotuvesId" required>
                                        <option value="">Pasirinkite...</option>
                                        ${parduotuves.map(p => `
                                            <option value="${p.id}">${p.miestas} - ${p.gatve}</option>
                                        `).join('')}
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Darbuotojas *</label>
                                    <select class="form-select" name="darbuotojoId" required>
                                        <option value="">Pasirinkite...</option>
                                        ${darbuotojai.map(d => `
                                            <option value="${d.id}">${d.vardas} ${d.pavarde}</option>
                                        `).join('')}
                                    </select>
                                </div>
                            </div>
                            
                            <hr>
                            <h6>Prekės</h6>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <input type="text" class="form-control" id="search-preke-sale" placeholder="Ieškoti prekės...">
                                </div>
                                <div class="col-md-3">
                                    <input type="number" class="form-control" id="preke-quantity" placeholder="Kiekis" value="1" min="0.001" step="0.001">
                                </div>
                                <div class="col-md-3">
                                    <button type="button" class="btn btn-success w-100" onclick="addToCart()">
                                        <i class="bi bi-plus"></i> Pridėti
                                    </button>
                                </div>
                            </div>
                            
                            <div id="cart-items"></div>
                            
                            <div class="cart-summary">
                                <div class="d-flex justify-content-between">
                                    <span>Iš viso:</span>
                                    <h4 id="cart-total">0.00 €</h4>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Atšaukti</button>
                        <button type="button" class="btn btn-primary" onclick="savePardavimas()">Užbaigti pardavimą</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    currentCart = [];
    showModal(modal);
    
    // Load darbuotojai if not loaded
    if (darbuotojai.length === 0) {
        fetch(`${API_BASE}/darbuotojai`)
            .then(r => r.json())
            .then(data => {
                darbuotojai = data;
                document.querySelector('[name="darbuotojoId"]').innerHTML = 
                    '<option value="">Pasirinkite...</option>' +
                    darbuotojai.map(d => `<option value="${d.id}">${d.vardas} ${d.pavarde}</option>`).join('');
            });
    }
    
    // Setup autocomplete for prekės search
    setupPrekeAutocomplete();
}

function setupPrekeAutocomplete() {
    const searchInput = document.getElementById('search-preke-sale');
    let autocompleteContainer = document.getElementById('preke-autocomplete');
    
    if (!autocompleteContainer) {
        autocompleteContainer = document.createElement('div');
        autocompleteContainer.id = 'preke-autocomplete';
        autocompleteContainer.className = 'autocomplete-container';
        searchInput.parentElement.style.position = 'relative';
        searchInput.parentElement.appendChild(autocompleteContainer);
    }
    
    searchInput.addEventListener('input', function() {
        const query = this.value.toLowerCase();
        
        if (query.length < 2) {
            autocompleteContainer.innerHTML = '';
            autocompleteContainer.style.display = 'none';
            return;
        }
        
        const matches = prekes.filter(p => 
            p.pavadinimas.toLowerCase().includes(query) ||
            (p.bruksninisKodas && p.bruksninisKodas.toLowerCase().includes(query))
        ).slice(0, 5);
        
        if (matches.length === 0) {
            autocompleteContainer.innerHTML = '';
            autocompleteContainer.style.display = 'none';
            return;
        }
        
        autocompleteContainer.innerHTML = matches.map(preke => `
            <div class="autocomplete-item" onclick="selectPreke('${preke.pavadinimas.replace(/'/g, "\\'")}')">
                <strong>${preke.pavadinimas}</strong><br>
                <small>${preke.bruksninisKodas || ''} - ${formatPrice(preke.pardavimoKaina)}</small>
            </div>
        `).join('');
        
        autocompleteContainer.style.display = 'block';
    });
    
    // Close autocomplete when clicking outside
    document.addEventListener('click', function(e) {
        if (e.target !== searchInput) {
            autocompleteContainer.style.display = 'none';
        }
    });
}

function selectPreke(pavadinimas) {
    const searchInput = document.getElementById('search-preke-sale');
    searchInput.value = pavadinimas;
    document.getElementById('preke-autocomplete').style.display = 'none';
    document.getElementById('preke-quantity').focus();
}

async function addToCart() {
    const searchInput = document.getElementById('search-preke-sale');
    const quantityInput = document.getElementById('preke-quantity');
    
    const prekePavadinimas = searchInput.value.trim();
    const quantity = parseFloat(quantityInput.value) || 1;
    
    if (!prekePavadinimas) {
        showError('Prašome įvesti prekės pavadinimą arba brūkšninį kodą');
        return;
    }
    
    // Load prekės if not loaded
    if (prekes.length === 0) {
        prekes = await fetch(`${API_BASE}/prekes`).then(r => r.json());
    }
    
    // Find preke
    const preke = prekes.find(p => 
        p.pavadinimas.toLowerCase().includes(prekePavadinimas.toLowerCase()) ||
        p.bruksninisKodas === prekePavadinimas
    );
    
    if (!preke) {
        showError(`Prekė "${prekePavadinimas}" nerasta sistemoje. Patikrinkite pavadinimą ar kodą.`);
        return;
    }
    
    // Check if already in cart
    const existingItem = currentCart.find(item => item.prekesId === preke.prekesId);
    
    if (existingItem) {
        existingItem.kiekis += quantity;
        existingItem.suma = existingItem.kiekis * existingItem.kaina;
    } else {
        currentCart.push({
            prekesId: preke.prekesId,
            pavadinimas: preke.pavadinimas,
            kaina: preke.pardavimoKaina,
            kiekis: quantity,
            suma: preke.pardavimoKaina * quantity
        });
    }
    
    // Clear inputs
    searchInput.value = '';
    quantityInput.value = 1;
    
    displayCart();
}

function displayCart() {
    const container = document.getElementById('cart-items');
    const totalElement = document.getElementById('cart-total');
    
    if (currentCart.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">Krepšelis tuščias</p>';
        totalElement.textContent = '0.00 €';
        return;
    }
    
    container.innerHTML = `
        <table class="table table-sm">
            <thead>
                <tr>
                    <th>Prekė</th>
                    <th>Kaina</th>
                    <th>Kiekis</th>
                    <th>Suma</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                ${currentCart.map((item, index) => `
                    <tr>
                        <td>${item.pavadinimas}</td>
                        <td>${formatPrice(item.kaina)}</td>
                        <td>
                            <input type="number" class="form-control form-control-sm" 
                                   value="${item.kiekis}" min="0.001" step="0.001"
                                   onchange="updateCartItem(${index}, this.value)" 
                                   style="width: 80px;">
                        </td>
                        <td><strong>${formatPrice(item.suma)}</strong></td>
                        <td>
                            <button class="btn btn-sm btn-danger" onclick="removeFromCart(${index})">
                                <i class="bi bi-x"></i>
                            </button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    const total = currentCart.reduce((sum, item) => sum + item.suma, 0);
    totalElement.textContent = formatPrice(total);
}

function updateCartItem(index, quantity) {
    const qty = parseFloat(quantity);
    if (qty < 0.001) {
        removeFromCart(index);
        return;
    }
    
    currentCart[index].kiekis = qty;
    currentCart[index].suma = currentCart[index].kaina * qty;
    displayCart();
}

function removeFromCart(index) {
    currentCart.splice(index, 1);
    displayCart();
}

async function savePardavimas() {
    const form = document.getElementById('pardavimasForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    if (currentCart.length === 0) {
        showError('Krepšelis tuščias. Pridėkite bent vieną prekę prieš užbaigiant pardavimą.');
        return;
    }
    
    const formData = new FormData(form);
    const bendraSuma = currentCart.reduce((sum, item) => sum + item.suma, 0);
    
    const pardavimasData = {
        parduotuvesId: parseInt(formData.get('parduotuvesId')),
        darbuotojoId: parseInt(formData.get('darbuotojoId')),
        dataLaikas: new Date().toISOString(),
        bendraSuma: bendraSuma,
        busena: 'uzbaigtas'
    };
    
    try {
        // Create pardavimas
        const pardavimasResponse = await fetch(`${API_BASE}/pardavimai`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(pardavimasData)
        });
        
        if (!pardavimasResponse.ok) {
            const errorText = await pardavimasResponse.text();
            showError(`Nepavyko sukurti pardavimo: ${errorText || 'nežinoma klaida'}`);
            return;
        }
        
        const pardavimas = await pardavimasResponse.json();
        
        // Create pardavimo eilutes
        for (const item of currentCart) {
            const eiluteData = {
                pardavimoId: pardavimas.pardavimoId,
                prekesId: item.prekesId,
                kiekis: item.kiekis,
                vienetoKaina: item.kaina,
                suma: item.suma
            };
            
            const eiluteResponse = await fetch(`${API_BASE}/pardavimo-eilutes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(eiluteData)
            });
            
            if (!eiluteResponse.ok) {
                const errorText = await eiluteResponse.text();
                showError(`Nepavyko pridėti prekės "${item.pavadinimas}": ${errorText || 'nežinoma klaida'}`);
                return;
            }
        }
        
        closeModal();
        showSuccess('Pardavimas sėkmingai užregistruotas');
        currentCart = [];
        loadPardavimai();
    } catch (error) {
        console.error('Klaida:', error);
        showError('Nepavyko išsaugoti pardavimo');
    }
}

async function viewPardavimas(pardavimoId) {
    try {
        const [pardavimas, eilutes] = await Promise.all([
            fetch(`${API_BASE}/pardavimai/${pardavimoId}`).then(r => r.json()),
            fetch(`${API_BASE}/pardavimo-eilutes/pardavimas/${pardavimoId}`).then(r => r.json())
        ]);
        
        const parduotuve = parduotuves.find(p => p.id === pardavimas.parduotuvesId);
        const darbuotojas = darbuotojai.find(d => d.id === pardavimas.darbuotojoId);
        
        const modal = `
            <div class="modal fade" id="viewPardavimasModal" tabindex="-1">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Pardavimas #${pardavimas.pardavimoId}</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <p><strong>Data:</strong> ${formatDateTime(pardavimas.dataLaikas)}</p>
                                    <p><strong>Parduotuvė:</strong> ${parduotuve ? `${parduotuve.miestas} - ${parduotuve.gatve}` : '-'}</p>
                                </div>
                                <div class="col-md-6">
                                    <p><strong>Darbuotojas:</strong> ${darbuotojas ? `${darbuotojas.vardas} ${darbuotojas.pavarde}` : '-'}</p>
                                    <p><strong>Būsena:</strong> 
                                        <span class="badge bg-${pardavimas.busena === 'uzbaigtas' ? 'success' : 'warning'}">
                                            ${pardavimas.busena || 'Naujas'}
                                        </span>
                                    </p>
                                </div>
                            </div>
                            
                            <h6>Prekės</h6>
                            <table class="table table-sm">
                                <thead>
                                    <tr>
                                        <th>Prekė</th>
                                        <th>Kaina</th>
                                        <th>Kiekis</th>
                                        <th>Suma</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${eilutes.map(e => {
                                        const preke = prekes.find(p => p.prekesId === e.prekesId);
                                        return `
                                            <tr>
                                                <td>${preke ? preke.pavadinimas : e.prekesId}</td>
                                                <td>${formatPrice(e.vienetoKaina)}</td>
                                                <td>${e.kiekis}</td>
                                                <td><strong>${formatPrice(e.suma)}</strong></td>
                                            </tr>
                                        `;
                                    }).join('')}
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <th colspan="3" class="text-end">Iš viso:</th>
                                        <th><h5>${formatPrice(pardavimas.bendraSuma)}</h5></th>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Uždaryti</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        showModal(modal);
    } catch (error) {
        console.error('Klaida kraunant pardavimo duomenis:', error);
        showError(`Nepavyko užkrauti pardavimo #${pardavimoId} duomenų: ${error.message}`);
    }
}

// Parduotuvės
async function loadParduotuves() {
    try {
        parduotuves = await fetch(`${API_BASE}/parduotuves`).then(r => r.json());
        displayParduotuves(parduotuves);
    } catch (error) {
        console.error('Klaida kraunant parduotuves:', error);
        showError('Nepavyko užkrauti parduotuvių');
    }
}

function displayParduotuves(items) {
    const tbody = document.getElementById('parduotuves-table');
    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Parduotuvių nerasta</td></tr>';
        return;
    }
    
    tbody.innerHTML = items.map(parduotuve => `
        <tr>
            <td>${parduotuve.id}</td>
            <td><strong>${parduotuve.miestas}</strong></td>
            <td>${parduotuve.gatve}</td>
            <td>${parduotuve.telefonas || '-'}</td>
            <td>${parduotuve.elPastas || '-'}</td>
            <td>
                <button class="btn btn-sm btn-info" onclick="editParduotuve(${parduotuve.id})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="deleteParduotuve(${parduotuve.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function showParduotuveModal(parduotuveId = null) {
    const isEdit = parduotuveId !== null;
    const parduotuve = isEdit ? parduotuves.find(p => p.id === parduotuveId) : {};
    
    const modal = `
        <div class="modal fade" id="parduotuveModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${isEdit ? 'Redaguoti parduotuvę' : 'Nauja parduotuvė'}</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="parduotuveForm">
                            <div class="mb-3">
                                <label class="form-label">Miestas *</label>
                                <input type="text" class="form-control" name="miestas" value="${parduotuve.miestas || ''}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Gatvė *</label>
                                <input type="text" class="form-control" name="gatve" value="${parduotuve.gatve || ''}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Telefonas</label>
                                <input type="tel" class="form-control" name="telefonas" value="${parduotuve.telefonas || ''}">
                            </div>
                            <div class="mb-3">
                                <label class="form-label">El. paštas</label>
                                <input type="email" class="form-control" name="elPastas" value="${parduotuve.elPastas || ''}">
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Atšaukti</button>
                        <button type="button" class="btn btn-primary" onclick="saveParduotuve(${parduotuveId})">Išsaugoti</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    showModal(modal);
}

async function saveParduotuve(parduotuveId) {
    const form = document.getElementById('parduotuveForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const formData = new FormData(form);
    const data = {
        miestas: formData.get('miestas'),
        gatve: formData.get('gatve'),
        telefonas: formData.get('telefonas'),
        elPastas: formData.get('elPastas')
    };
    
    try {
        const url = parduotuveId ? `${API_BASE}/parduotuves/${parduotuveId}` : `${API_BASE}/parduotuves`;
        const method = parduotuveId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            closeModal();
            showSuccess(parduotuveId ? 'Parduotuvė atnaujinta' : 'Parduotuvė sukurta');
            loadParduotuves();
            loadInitialData();
        } else {
            const errorText = await response.text();
            showError(`Nepavyko išsaugoti parduotuvės: ${errorText || 'nežinoma klaida'}`);
        }
    } catch (error) {
        console.error('Klaida:', error);
        showError('Nepavyko išsaugoti parduotuvės');
    }
}

function editParduotuve(parduotuveId) {
    showParduotuveModal(parduotuveId);
}

async function deleteParduotuve(parduotuveId) {
    if (!confirm('Ar tikrai norite ištrinti šią parduotuvę?')) return;
    
    try {
        const response = await fetch(`${API_BASE}/parduotuves/${parduotuveId}`, { method: 'DELETE' });
        if (response.ok) {
            showSuccess('Parduotuvė ištrinta');
            loadParduotuves();
            loadInitialData();
        } else {
            const errorText = await response.text();
            if (response.status === 409 || errorText.includes('constraint')) {
                showError('Parduotuvė negali būti ištrinta - jai priskirti darbuotojai ar inventorius');
            } else {
                showError(`Nepavyko ištrinti parduotuvės: ${errorText || 'nežinoma klaida'}`);
            }
        }
    } catch (error) {
        console.error('Klaida trinant parduotuvę:', error);
        showError('Ryšio klaida - nepavyko ištrinti parduotuvės');
    }
}

// Darbuotojai
async function loadDarbuotojai() {
    try {
        darbuotojai = await fetch(`${API_BASE}/darbuotojai`).then(r => r.json());
        displayDarbuotojai(darbuotojai);
    } catch (error) {
        console.error('Klaida kraunant darbuotojus:', error);
        showError('Nepavyko užkrauti darbuotojų');
    }
}

function displayDarbuotojai(items) {
    const tbody = document.getElementById('darbuotojai-table');
    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center text-muted">Darbuotojų nerasta</td></tr>';
        return;
    }
    
    tbody.innerHTML = items.map(darbuotojas => {
        const parduotuve = parduotuves.find(p => p.id === darbuotojas.parduotuvesId);
        const pareiga = pareigos.find(p => p.pareiguId === darbuotojas.pareiguId);
        
        return `
            <tr>
                <td>${darbuotojas.id}</td>
                <td>${darbuotojas.vardas}</td>
                <td>${darbuotojas.pavarde}</td>
                <td>${darbuotojas.asmensKodas || '-'}</td>
                <td>${pareiga ? pareiga.pavadinimas : '-'}</td>
                <td>${parduotuve ? `${parduotuve.miestas} - ${parduotuve.gatve}` : '-'}</td>
                <td>${darbuotojas.telefonas}</td>
                <td>${darbuotojas.elPastas || '-'}</td>
                <td>
                    <button class="btn btn-sm btn-info" onclick="editDarbuotojas(${darbuotojas.id})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteDarbuotojas(${darbuotojas.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

async function showDarbuotojasModal(darbuotojasId = null) {
    const isEdit = darbuotojasId !== null;
    let darbuotojas = {};
    
    if (isEdit) {
        darbuotojas = darbuotojai.find(d => d.id === darbuotojasId) || {};
    }
    
    const modal = `
        <div class="modal fade" id="darbuotojasModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${isEdit ? 'Redaguoti darbuotoją' : 'Naujas darbuotojas'}</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="darbuotojasForm">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Vardas *</label>
                                    <input type="text" class="form-control" name="vardas" value="${darbuotojas.vardas || ''}" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Pavardė *</label>
                                    <input type="text" class="form-control" name="pavarde" value="${darbuotojas.pavarde || ''}" required>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Asmens kodas *</label>
                                    <input type="text" class="form-control" name="asmensKodas" value="${darbuotojas.asmensKodas || ''}" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Telefonas *</label>
                                    <input type="tel" class="form-control" name="telefonas" value="${darbuotojas.telefonas || ''}" required>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">El. paštas</label>
                                <input type="email" class="form-control" name="elPastas" value="${darbuotojas.elPastas || ''}">
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Pareigos *</label>
                                    <select class="form-select" name="pareiguId" required>
                                        <option value="">Pasirinkite...</option>
                                        ${pareigos.map(p => `
                                            <option value="${p.pareiguId}" ${darbuotojas.pareiguId === p.pareiguId ? 'selected' : ''}>
                                                ${p.pavadinimas}
                                            </option>
                                        `).join('')}
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Parduotuvė *</label>
                                    <select class="form-select" name="parduotuvesId" required>
                                        <option value="">Pasirinkite...</option>
                                        ${parduotuves.map(p => `
                                            <option value="${p.id}" ${darbuotojas.parduotuvesId === p.id ? 'selected' : ''}>
                                                ${p.miestas} - ${p.gatve}
                                            </option>
                                        `).join('')}
                                    </select>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Įdarbinimo data *</label>
                                    <input type="date" class="form-control" name="idarbinimoData" value="${darbuotojas.idarbinimoData || ''}" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Valandinis atlyginimas (€) *</label>
                                    <input type="number" step="0.01" min="0" class="form-control" name="valandinisAtlyginimas" value="${darbuotojas.valandinisAtlyginimas || ''}" required>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Atšaukti</button>
                        <button type="button" class="btn btn-primary" onclick="saveDarbuotojas(${darbuotojasId})">Išsaugoti</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    showModal(modal);
}

async function saveDarbuotojas(darbuotojasId) {
    const form = document.getElementById('darbuotojasForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const formData = new FormData(form);
    const data = {
        vardas: formData.get('vardas'),
        pavarde: formData.get('pavarde'),
        asmensKodas: formData.get('asmensKodas'),
        telefonas: formData.get('telefonas'),
        elPastas: formData.get('elPastas'),
        pareiguId: parseInt(formData.get('pareiguId')),
        parduotuvesId: parseInt(formData.get('parduotuvesId')),
        idarbinimoData: formData.get('idarbinimoData'),
        valandinisAtlyginimas: parseFloat(formData.get('valandinisAtlyginimas'))
    };
    
    try {
        const url = darbuotojasId ? `${API_BASE}/darbuotojai/${darbuotojasId}` : `${API_BASE}/darbuotojai`;
        const method = darbuotojasId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            closeModal();
            showSuccess(darbuotojasId ? 'Darbuotojas atnaujintas' : 'Darbuotojas sukurtas');
            loadDarbuotojai();
        } else {
            const errorText = await response.text();
            if (response.status === 400 && errorText.includes('asmens')) {
                showError('Darbuotojas su tokiu asmens kodu jau egzistuoja');
            } else {
                showError(`Nepavyko išsaugoti darbuotojo: ${errorText || 'nežinoma klaida'}`);
            }
        }
    } catch (error) {
        console.error('Klaida:', error);
        showError('Nepavyko išsaugoti darbuotojo');
    }
}

function editDarbuotojas(darbuotojasId) {
    showDarbuotojasModal(darbuotojasId);
}

async function deleteDarbuotojas(darbuotojasId) {
    if (!confirm('Ar tikrai norite ištrinti šį darbuotoją?')) return;
    
    try {
        const response = await fetch(`${API_BASE}/darbuotojai/${darbuotojasId}`, { method: 'DELETE' });
        if (response.ok) {
            showSuccess('Darbuotojas ištrintas');
            loadDarbuotojai();
        } else {
            const errorText = await response.text();
            if (response.status === 409 || errorText.includes('constraint')) {
                showError('Darbuotojas negali būti ištrintas - jam priskirti pardavimai');
            } else {
                showError(`Nepavyko ištrinti darbuotojo: ${errorText || 'nežinoma klaida'}`);
            }
        }
    } catch (error) {
        console.error('Klaida trinant darbuotoją:', error);
        showError('Ryšio klaida - nepavyko ištrinti darbuotojo');
    }
}

// Pareigos
async function loadPareigos() {
    try {
        pareigos = await fetch(`${API_BASE}/pareigos`).then(r => r.json());
        displayPareigos(pareigos);
    } catch (error) {
        console.error('Klaida kraunant pareigas:', error);
        showError('Nepavyko užkrauti pareigų');
    }
}

function displayPareigos(items) {
    const tbody = document.getElementById('pareigos-table');
    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Pareigų nerasta</td></tr>';
        return;
    }
    
    tbody.innerHTML = items.map(pareiga => `
        <tr>
            <td>${pareiga.pareiguId}</td>
            <td><strong>${pareiga.pavadinimas}</strong></td>
            <td>${pareiga.aprasymas || '-'}</td>
            <td>
                <button class="btn btn-sm btn-info" onclick="editPareigos(${pareiga.pareiguId})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="deletePareigos(${pareiga.pareiguId})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function showPareigosModal(pareiguId = null) {
    const isEdit = pareiguId !== null;
    let pareiga = {};
    
    if (isEdit) {
        pareiga = pareigos.find(p => p.pareiguId === pareiguId) || {};
    }
    
    const modal = `
        <div class="modal fade" id="pareigosModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${isEdit ? 'Redaguoti pareigas' : 'Naujos pareigos'}</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="pareigosForm">
                            <div class="mb-3">
                                <label class="form-label">Pavadinimas *</label>
                                <input type="text" class="form-control" name="pavadinimas" value="${pareiga.pavadinimas || ''}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Aprašymas</label>
                                <textarea class="form-control" name="aprasymas" rows="2">${pareiga.aprasymas || ''}</textarea>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Atšaukti</button>
                        <button type="button" class="btn btn-primary" onclick="savePareigos(${pareiguId})">Išsaugoti</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    showModal(modal);
}

async function savePareigos(pareiguId) {
    const form = document.getElementById('pareigosForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const formData = new FormData(form);
    const data = {
        pavadinimas: formData.get('pavadinimas'),
        aprasymas: formData.get('aprasymas')
    };
    
    try {
        const url = pareiguId ? `${API_BASE}/pareigos/${pareiguId}` : `${API_BASE}/pareigos`;
        const method = pareiguId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            closeModal();
            showSuccess(pareiguId ? 'Pareigos atnaujintos' : 'Pareigos sukurtos');
            loadPareigos();
            loadInitialData();
        } else {
            const errorText = await response.text();
            showError(`Nepavyko išsaugoti pareigų: ${errorText || 'nežinoma klaida'}`);
        }
    } catch (error) {
        console.error('Klaida:', error);
        showError('Nepavyko išsaugoti pareigų');
    }
}

function editPareigos(pareiguId) {
    showPareigosModal(pareiguId);
}

async function deletePareigos(pareiguId) {
    if (!confirm('Ar tikrai norite ištrinti šias pareigas?')) return;
    
    try {
        const response = await fetch(`${API_BASE}/pareigos/${pareiguId}`, { method: 'DELETE' });
        if (response.ok) {
            showSuccess('Pareigos ištrintos');
            loadPareigos();
            loadInitialData();
        } else {
            const errorText = await response.text();
            if (response.status === 409 || errorText.includes('constraint')) {
                showError('Pareigos negali būti ištrintos - joms priskirti darbuotojai');
            } else {
                showError(`Nepavyko ištrinti pareigų: ${errorText || 'nežinoma klaida'}`);
            }
        }
    } catch (error) {
        console.error('Klaida trinant pareigas:', error);
        showError('Ryšio klaida - nepavyko ištrinti pareigų');
    }
}

// Utility functions
function showModal(modalHtml) {
    const container = document.getElementById('modal-container');
    container.innerHTML = modalHtml;
    const modal = new bootstrap.Modal(container.querySelector('.modal'));
    modal.show();
}

function closeModal() {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        const bsModal = bootstrap.Modal.getInstance(modal);
        if (bsModal) bsModal.hide();
    });
}

function formatPrice(price) {
    if (!price) return '0.00 €';
    return parseFloat(price).toFixed(2) + ' €';
}

function formatDateTime(dateTime) {
    if (!dateTime) return '-';
    const date = new Date(dateTime);
    return date.toLocaleString('lt-LT');
}

function showSuccess(message) {
    alert('✓ ' + message);
}

function showError(message) {
    alert('✗ ' + message);
}

function showInfo(message) {
    alert('ℹ ' + message);
}

// ==================== Pardavimo Eilutės ====================

async function loadPardavimoEilutes() {
    try {
        // Krauname visus duomenis
        const [eilutes, pardavimaiData, prekesData, parduotuvesData] = await Promise.all([
            fetch(`${API_BASE}/pardavimo-eilutes`).then(r => r.json()),
            fetch(`${API_BASE}/pardavimai`).then(r => r.json()),
            fetch(`${API_BASE}/prekes`).then(r => r.json()),
            fetch(`${API_BASE}/parduotuves`).then(r => r.json())
        ]);
        
        displayPardavimoEilutes(eilutes, pardavimaiData, prekesData, parduotuvesData);
    } catch (error) {
        console.error('Klaida kraunant pardavimo eilutes:', error);
        showError('Nepavyko užkrauti pardavimo eilučių');
    }
}

function displayPardavimoEilutes(eilutes, pardavimaiData, prekesData, parduotuvesData) {
    const tbody = document.getElementById('pardavimo-eilutes-table');
    
    if (eilutes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">Pardavimo eilučių nerasta</td></tr>';
        return;
    }
    
    tbody.innerHTML = eilutes.map(eilute => {
        const pardavimas = pardavimaiData.find(p => p.pardavimoId === eilute.pardavimoId);
        const preke = prekesData.find(p => p.prekesId === eilute.prekesId);
        const parduotuve = parduotuvesData.find(p => p.id === pardavimas?.parduotuvesId);
        
        return `
            <tr>
                <td>${eilute.eilutesId}</td>
                <td><a href="#" onclick="showPage('pardavimai'); return false;">${eilute.pardavimoId}</a></td>
                <td>${pardavimas ? formatDateTime(pardavimas.dataLaikas) : '-'}</td>
                <td>${parduotuve ? `${parduotuve.miestas} - ${parduotuve.gatve}` : '-'}</td>
                <td>${preke ? preke.pavadinimas : `ID: ${eilute.prekesId}`}</td>
                <td>${eilute.kiekis} ${preke?.matoVienetas || ''}</td>
                <td>${formatPrice(eilute.vienetoKaina)}</td>
                <td><strong>${formatPrice(eilute.suma)}</strong></td>
            </tr>
        `;
    }).join('');
}
