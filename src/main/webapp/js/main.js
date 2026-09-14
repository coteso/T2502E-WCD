// =========================================================
// MVC Product Demo - basic client-side JS
// =========================================================

// Xac nhan truoc khi xoa san pham
function confirmDelete(productName) {
    return window.confirm("Ban co chac muon xoa san pham \"" + productName + "\" khong?");
}

// Validate don gian cho form them/sua san pham truoc khi submit
function validateProductForm() {
    const name = document.getElementById("name");
    const price = document.getElementById("price");
    const quantity = document.getElementById("quantity");
    let errors = [];

    if (!name.value.trim()) {
        errors.push("Ten san pham khong duoc de trong.");
    }
    if (price.value === "" || Number(price.value) < 0) {
        errors.push("Gia san pham phai >= 0.");
    }
    if (quantity.value === "" || Number(quantity.value) < 0) {
        errors.push("So luong phai >= 0.");
    }

    if (errors.length > 0) {
        alert(errors.join("\n"));
        return false;
    }
    return true;
}

// Tu dong an alert thanh cong sau 3 giay
document.addEventListener("DOMContentLoaded", function () {
    const successAlert = document.querySelector(".alert-success");
    if (successAlert) {
        setTimeout(function () {
            successAlert.style.display = "none";
        }, 3000);
    }
});
