var darkModeToggle = document.getElementById('dark-mode-1');
var modeText = document.getElementById('mode-text');
var logos = document.querySelectorAll('.logo-toggle');

if (darkModeToggle && modeText) {
    darkModeToggle.addEventListener('change', () => {
        document.body.classList.toggle('dark-mode-1', darkModeToggle.checked);
        modeText.textContent = darkModeToggle.checked ? "DARK" : "Light"; 

        logos.forEach(logo => {
            logo.src = darkModeToggle.checked ? 'images/logo2.png' : 'images/logo1.png';
        });
    });
}

function displayCurrentWeek() {
    var today = new Date();
    var dayOfWeek = today.getDay(); 
    var daysFromSunday = dayOfWeek;
    var startOfWeek = new Date(today);
    startOfWeek.setDate(today.getDate() - daysFromSunday);

    var options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    var formattedDate = startOfWeek.toLocaleDateString('en-US', options);
    
    var weekStartDateElement = document.getElementById("weekStartDate");
    if (weekStartDateElement) {  // Only set textContent if the element exists
        weekStartDateElement.textContent = `This week starts on: ${formattedDate}`;
    }
}

document.addEventListener("DOMContentLoaded", function() {
    displayCurrentWeek();
});

function showMoreOffers() {
    var hiddenOffers = document.querySelectorAll('.promotion-item-1.hidden');
    var offersToShow = Math.min(3, hiddenOffers.length);

    for (let i = 0; i < 3 && i < hiddenOffers.length; i++) {
        hiddenOffers[i].classList.remove('hidden');
    }
    if (hiddenOffers.length <= offersToShow) {
        document.getElementById('show').style.display = 'none'; // Ø¥Ø®ÙØ§Ø¡ Ø§Ù„Ø²Ø± Ø¥Ø°Ø§ Ø§Ù†ØªÙ‡Øª Ø§Ù„Ø¹Ø±ÙˆØ¶
    }
}

document.addEventListener('DOMContentLoaded', () => {
    var reviews = document.querySelectorAll('.review-1');
    var tooltip = document.getElementById('tooltip');

    reviews.forEach(review => {
        review.addEventListener('mouseover', event => {
            // Get data attributes for the current review
            var customer = review.getAttribute('data-customer');
            var product = review.getAttribute('data-product');
            var rate = review.getAttribute('data-rate');
            var feedback = review.getAttribute('data-feedback');

            // Set the tooltip content
            tooltip.innerHTML = `<strong>Customer:</strong> ${customer}<br>
                                 <strong>Product:</strong> ${product}<br>
                                 <strong>Rating:</strong> ${rate}<br>
                                 <strong>Feedback:</strong> ${feedback}`;

            // Position the tooltip near the mouse pointer
            tooltip.style.display = 'block';
            tooltip.style.left = `${event.pageX + 10}px`;
            tooltip.style.top = `${event.pageY + 10}px`;
        });

        review.addEventListener('mousemove', event => {
            // Update tooltip position as mouse moves
            tooltip.style.left = `${event.pageX + 10}px`;
            tooltip.style.top = `${event.pageY + 10}px`;
        });

        review.addEventListener('mouseout', () => {
            tooltip.style.display = 'none'; // Hide tooltip when mouse leaves
        });
    });
});


function displayBookingDetails() {
    var TotalPrice = localStorage.getItem('TotalPrice');
    
    // Log the value to verify it's retrieved correctly
    console.log('Total Price retrieved from localStorage:', TotalPrice);
    
    
    // Set today's date
    var today = new Date();
    var formattedDateTime = today.toLocaleString('en-GB', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
    });

    // Set booking date on the confirmation page
    var bookingDateElement = document.getElementById('bookingDate');
    if (bookingDateElement) {
        bookingDateElement.textContent = formattedDateTime;
    }

    // Set total price on the confirmation page
    var totalPriceElement = document.getElementById('totalPrice-bc');
    if (totalPriceElement) {
        totalPriceElement.textContent = `Total Price: ${TotalPrice} SAR`;
        totalPriceElement.style.fontFamily = 'roboto';
    }
}

window.addEventListener('DOMContentLoaded', function() {
    displayBookingDetails();
});


// Cart page
let cartItems = {}; 


function handleCartDisplay() {
    var storedCart = localStorage.getItem('cart');
    var cartDisplay = document.getElementById('cartList');
    var cartMessage = document.getElementById('cart-message');
    var orderSummary = document.getElementById('orderSummary');
    var emptyCartButton = document.getElementById('empty-cart-btn');

    if (storedCart && storedCart !== '[]' && storedCart !== '{}') {
         cartItems = JSON.parse(storedCart);
		 
		 var discount = parseFloat(localStorage.getItem('appliedDiscount')) || 0;
		 
		 if(cartMessage){
		 cartMessage.textContent = '';}
		 
		 if(cartDisplay){
        cartDisplay.style.display = 'block';
		cartDisplay.innerHTML = '';
		 }
		 if(orderSummary){
        orderSummary.style.display = 'block';
		 }
		 if(emptyCartButton){
        emptyCartButton.style.display = 'block';
		 }

        let priceTotal=0;
        let itemCount = 0;

        var tableHtml = '<table class="table-cartPage">' +
            '<thead style="text-align:left">' +
                '<tr>' +
                    '<th></th>' +
                    '<th>Product</th>' +
                    '<th>Price</th>' +
                    '<th>Number Of Nights</th>' +
                    '<th>Date</th>' +
                    '<th>Total</th>' +
                '</tr>' +
            '</thead>' +
            '<tbody id="cartList" class="orders-cartPage">';
         if(cartDisplay)
        cartDisplay.innerHTML = tableHtml;

        for (var productName in cartItems) {
            if (cartItems.hasOwnProperty(productName)) {
                var item = cartItems[productName];
                var itemTotal = item.price * item.quantity;

                var rowHtml = '<tr data-product="' + productName + '">' +
                    '<td>' +
                        '<img src="' + item.image + '" alt="' + productName + '" class="product-image-cartPage">' +
                        '<div>' +
                            '<span class="remove-cartPage">Remove</span>' +
                        '</div>' +
                    '</td>' +
                    '<td>' + productName + '</td>' +
                    '<td>' + item.price + ' SAR</td>' +
                    '<td>' +
                        '<div class="quantity-wrapper-cartPage">' +
                            '<button class="minus-btn-cartPage">-</button>' +
                            '<p class="quantity-circle-cartPage">' + item.quantity + '</p>' +
                            '<button class="plus-btn-cartPage">+</button>' +
                        '</div>' +
                    '</td>' +
                    '<td>' +
                        '<div class="date-range-container-cartPage">' +
                            '<label for="start-date' + itemCount + '-cartPage">Start Date:</label>' +
                            '<input type="date" id="start-date' + itemCount + '-cartPage" class="date-input-cartPage" value="' + item.startDate + '"><br>' +
                            '<label for="end-date' + itemCount + '-cartPage">End Date:</label>' +
                            '<input type="date" id="end-date' + itemCount + '-cartPage" class="date-input-cartPage" value="' + item.endDate + '">' +
                        '</div>' +
                    '</td>' +
                    '<td>' + itemTotal.toFixed(2) + ' SAR</td>' +
                '</tr>';
                 if(cartDisplay)
                cartDisplay.innerHTML += rowHtml;
                priceTotal += itemTotal;
                itemCount++;
				
            }
        }
         if(cartDisplay)
        cartDisplay.innerHTML += '</tbody></table>';
        updateOrderSummary(cartItems, discount);

        // Add event listeners for date inputs to update localStorage
		if(cartDisplay)
cartDisplay.querySelectorAll('.date-input-cartPage').forEach((input, index) => {
    input.addEventListener('change', function() {
        var row = input.closest('tr');
        var productName = row.getAttribute('data-product');
        var startDateInput = row.querySelector('input[id^="start-date"]');
        var endDateInput = row.querySelector('input[id^="end-date"]');
        var startDate = startDateInput.value;
        var endDate = endDateInput.value;

        // Convert to Date objects for comparison
        var start = new Date(startDate);
        var end = new Date(endDate);
        var today = new Date();
        
        // Clear time from today's date for accurate comparison
        today.setHours(0, 0, 0, 0);

        // Validate start and end dates
        if (startDate && endDate) {
            // Check that the start date is today or in the future
            if (start < today) {
                alert('Please select a Valid start date ');
                startDateInput.value = '';  // Clear invalid start date
                return;
            }

            // Check that the end date is after the start date
            if (end <= start) {
                alert('Please select a Valid End Date');
                endDateInput.value = '';  // Clear invalid end date
                return;
            }

            // Dates are valid, update the cart
            var storedCart = localStorage.getItem('cart');
            if (storedCart) {
                var cartItems = JSON.parse(storedCart);

                if (cartItems[productName]) {
                    var timeDifference = end - start;
                    var daysDifference = timeDifference / (1000 * 3600 * 24); // Convert milliseconds to days

                    cartItems[productName].startDate = startDate;
                    cartItems[productName].endDate = endDate;
                    cartItems[productName].quantity = daysDifference; // Update quantity based on the new date range
                }

                // Update the cart in localStorage after modifying dates
                localStorage.setItem('cart', JSON.stringify(cartItems));
                handleCartDisplay(); // Refresh cart display
                updateOrderSummary(cartItems, 0); // Update order summary
            }
        } else {
            alert('Please select both start and end dates.');
        }
    });
		 });

      
             if(cartDisplay)
        cartDisplay.querySelectorAll('.minus-btn-cartPage').forEach(button => {
            button.addEventListener('click', decreaseQuantity);
        });
           if(cartDisplay)
        cartDisplay.querySelectorAll('.plus-btn-cartPage').forEach(button => {
            button.addEventListener('click', increaseQuantity);
        });
           if(cartDisplay)
        cartDisplay.querySelectorAll('.remove-cartPage').forEach(button => {
            button.addEventListener('click', removeItem);
        });
        if(emptyCartButton)
        emptyCartButton.addEventListener('click', clearCart);
    } else {
		if(cartMessage)
        cartMessage.textContent = 'Your Cart is Empty';
	if(cartDisplay)
        cartDisplay.style.display = 'none';
	if(orderSummary)
        orderSummary.style.display = 'none';
	if(emptyCartButton)
        emptyCartButton.style.display = 'none';
    }
}



// Function to update the cart in localStorage and refresh the page
function updateCart() {
    localStorage.setItem('cart', JSON.stringify(cartItems));
    handleCartDisplay();
}


function decreaseQuantity(event) {
    var productRow = event.target.closest('tr');
    var productName = productRow.dataset.product;

    // Retrieve the current start date for the product
    var startDate = new Date(cartItems[productName].startDate);

    if (cartItems[productName].quantity > 1) {
        // Decrease quantity (number of nights)
        cartItems[productName].quantity--;

        // Calculate the new end date based on updated quantity
        var newEndDate = new Date(startDate);
        newEndDate.setDate(newEndDate.getDate() + cartItems[productName].quantity);

        // Update the end date in the cart item
        cartItems[productName].endDate = newEndDate.toISOString().split('T')[0]; // Format as yyyy-mm-dd

        // Update the cart in local storage and display
        updateCart();
    }
}

function increaseQuantity(event) {
    var productRow = event.target.closest('tr');
    var productName = productRow.dataset.product;

    // Retrieve the current start date for the product
    var startDate = new Date(cartItems[productName].startDate);

    // Increase quantity (number of nights)
    cartItems[productName].quantity++;

    // Calculate the new end date based on updated quantity
    var newEndDate = new Date(startDate);
    newEndDate.setDate(newEndDate.getDate() + cartItems[productName].quantity);

    // Update the end date in the cart item
    cartItems[productName].endDate = newEndDate.toISOString().split('T')[0]; // Format as yyyy-mm-dd

    // Update the cart in local storage and display
    updateCart();
}

function removeItem(event) {
    var productRow = event.target.closest('tr');
    var productName = productRow.dataset.product;
    delete cartItems[productName];
    updateCart();
}

function clearCart() {
	localStorage.removeItem('cart');
	localStorage.removeItem('appliedDiscount');

    cartItems = {};
    updateCart();
}

function handleCheckout(){
    clearCart();

 
    window.location.href = 'booking-confirmation.html';
}



// Initialize cart display on page load and on cart page link click
document.addEventListener('DOMContentLoaded', handleCartDisplay);
document.getElementById('cart-page-link').addEventListener('click', handleCartDisplay);

// Function to add a product in recomenned section to the cart in local storage
function addToCart1(image, name, quantity, price) {
    var today = new Date();
    var startDate = today.toISOString().split('T')[0];  // Set the start date as today's date

    let cart = JSON.parse(localStorage.getItem('cart')) || {}; // Retrieve existing cart or initialize a new one

    // If item already exists in the cart, update its quantity
    if (cart[name]) { // Use the 'name' parameter for productName
        cart[name].quantity += quantity;

        // Update the end date based on the updated quantity
        var start = new Date(cart[name].startDate);  // Use the stored start date
        var updatedEndDate = new Date(start);
        updatedEndDate.setDate(start.getDate() + cart[name].quantity);  // Recalculate end date based on the updated quantity

        cart[name].endDate = updatedEndDate.toISOString().split('T')[0]; // Update the end date
    } else {
        // Calculate initial end date based on quantity
        var initialEndDate = new Date(today);
        initialEndDate.setDate(today.getDate() + quantity);

        // Format end date as a string
        var endDateString = initialEndDate.toISOString().split('T')[0];

        // Otherwise, add new product to the cart
        cart[name] = {
            image: image,
            quantity: quantity,
            price: price,
            startDate: startDate,
            endDate: endDateString  
        };
    }

    // Save the updated cart back to localStorage
    localStorage.setItem('cart', JSON.stringify(cart));

    // Redirect to the cart page
    window.location.href = 'cart.html';
}






// Update the order summary (with or without discount)
function updateOrderSummary(cartItems, discount) {
    
    let summaryHtml = '';
    let totalPricecartPage=0;
    // Calculate the total price of all items
    for (var productName in cartItems) {
        var item = cartItems[productName];
        var itemTotal = item.price * item.quantity;
        totalPricecartPage += itemTotal;

        summaryHtml += '<p class="item-header-cartPage">' + productName + '</p>' +
            '<span class="item-sub-cartPage">' + item.price + ' SAR per night </span>' +
            '<span class="item-sub-cartPage">x ' + item.quantity + ' =</span>' +
            '<span class="item-total-cartPage">' + itemTotal.toFixed(2) + ' SAR</span>';
    }

    // Apply the discount
    var discountAmount = totalPricecartPage * discount;
    totalPricecartPage -= discountAmount;

      // Save total price to localStorage
    localStorage.setItem('TotalPrice', totalPricecartPage);

    // Call displayBookingDetails with the total price
    displayBookingDetails(totalPricecartPage);
	
    // Update the summary HTML with the discount and new total price
    summaryHtml += '<hr style="border: 1px solid #ccc; margin: 10px 0;">' +
        '<p class="item-header-cartPage">Discount: ' + discountAmount.toFixed(2) + ' SAR</p>' +
		
        '<p class="total-price-cartPage" style="font-size:17px;">Total: ' + totalPricecartPage.toFixed(2) + ' SAR</p>';
           
 var orderSummary = document.getElementById('orderSummary');
    if (orderSummary) {
        var itemsList = orderSummary.querySelector('.items-list-cartPage');
        if (itemsList) {
            itemsList.innerHTML = summaryHtml;
        }
    } 

}

// Function to apply discount
function applyDiscount() {
    var discountField = document.getElementById("discountCode");
    var discountMessage = document.getElementById("discountAlert");
    var cartItems = JSON.parse(localStorage.getItem('cart')) || {}; 


    // Predefined discount codes and discount values
    var discountCode1 = 'darek';// 10% discount
    var discountCode2 = 'dr.sahar';// 20% discount
    var discountCode3 = 'itFairWinner';// 30% discount

    let discount = 0;
	
    var enteredCode = discountField.value.trim().toLowerCase();
 
    if (enteredCode === discountCode1) {
        discount = 0.10;
    } else if (enteredCode === discountCode2) {
        discount = 0.20;
    } else if (enteredCode === discountCode3) {
        discount = 0.30;
    }

    if (discount > 0) {
        discountMessage.textContent = "Discount applied successfully!";
        discountMessage.style.fontSize = "0.7em";
        discountMessage.style.color = "green"; // Success message color
		// Save discount to localStorage
        localStorage.setItem('appliedDiscount', discount);
    } else {
        discountMessage.textContent = "Incorrect discount code.";
        discountMessage.style.fontSize = "0.7em";
        discountMessage.style.color = "red"; // Error message color
    }

    // Update the order summary with the new total price after discount
    updateOrderSummary(cartItems, discount);

    // Clear the input field after applying
    discountField.value = "";
}

// Attach event listener to apply discount button
var applyDiscountBtn=document.getElementById("applyDiscountBtn")
if(applyDiscountBtn)
applyDiscountBtn.addEventListener("click", applyDiscount);


//booking history 

function filterBookings(status) {
    // Get all booking boxes and tabs
    var bookingBoxes = document.querySelectorAll('.booking-box-bh');
    var tabs = document.querySelectorAll('.tabs-bh a');

    // Hide all booking boxes initially
    bookingBoxes.forEach(box => {
        if (status === 'all') {
            box.style.display = 'block'; // Show all bookings if "All Bookings" is selected
        } else {
            box.style.display = box.getAttribute('data-status') === status ? 'block' : 'none';
        }
    });

    // Remove active class from all tabs
    tabs.forEach(tab => tab.classList.remove('active-bh'));

    // Add active class to the clicked tab
    var activeTab = Array.from(tabs).find(tab => tab.textContent.toLowerCase() === status || (status === 'all' && tab.textContent === 'All Bookings'));
    if (activeTab) {
        activeTab.classList.add('active-bh');
    }
}

// script for catgory page
// Function to add a product to the cart in local storage
function addToCart(productName, image, quantity, price, startDate, endDateString) {
    let cart = JSON.parse(localStorage.getItem('cart')) || {};

    // If the product already exists in the cart, update its quantity and end date
    if (cart[productName]) {
        cart[productName].quantity += quantity;

        // Update the end date based on the updated quantity
        var today = new Date(startDate);  // Start from the provided start date
        var updatedEndDate = new Date(today);
        updatedEndDate.setDate(today.getDate() + cart[productName].quantity);  // Recalculate end date based on the updated quantity

        cart[productName].endDate = updatedEndDate.toISOString().split('T')[0]; // Update the end date
    } else {
        // Otherwise, add new product to the cart
        cart[productName] = {
            image: image,
            quantity: quantity,
            price: price,
            startDate: startDate,
            endDate: endDateString  
        };
    }

    // Save the updated cart to local storage
    localStorage.setItem('cart', JSON.stringify(cart));
}

// Handle individual "Add to Cart" button click inside each product card
document.querySelectorAll('.add-to-cart-image').forEach(button => {
    button.addEventListener('click', function () {
        // Get the product card element that the button belongs to
        var productCard = this.closest('.card-catagoryPage');  // Find the card container

        // Extract product image and product name and price from data attributes
        var productImage = productCard.getAttribute('data-image');
        var productName = productCard.getAttribute('data-name');
        var productPrice = parseFloat(productCard.getAttribute('data-price'));

        // Get today's date for start date
        var today = new Date();
        var startDate = today.toISOString().split('T')[0]; // Format as YYYY-MM-DD

        // Get the quantity input field and its value
        var quantityInput = productCard.querySelector('.quantity-input');
        var quantity = parseInt(quantityInput.value);

        // Calculate the end date by adding the quantity (number of nights) to the start date
        var endDate = new Date(today);
        endDate.setDate(today.getDate() + quantity);  // Add the quantity to today's date
        var endDateString = endDate.toISOString().split('T')[0]; // Format end date as YYYY-MM-DD

        // Add product to the cart
        addToCart(productName, productImage, quantity, productPrice, startDate, endDateString);

        // Show confirmation message
        var confirmationMessage = document.createElement('div');
        confirmationMessage.id = 'confirmation-message';
        confirmationMessage.innerHTML = `${quantity} x ${productName} added to the cart!`;
        confirmationMessage.style.position = 'fixed';
        confirmationMessage.style.top = '20px';
        confirmationMessage.style.left = '50%';
        confirmationMessage.style.transform = 'translateX(-50%)';
        confirmationMessage.style.backgroundColor = '#4CAF50';
        confirmationMessage.style.color = '#fff';
        confirmationMessage.style.padding = '10px';
        confirmationMessage.style.borderRadius = '5px';
        confirmationMessage.style.display = 'block';
        document.body.appendChild(confirmationMessage);

        // Hide the confirmation message after 2 seconds
        setTimeout(() => {
            confirmationMessage.style.display = 'none';
			
        }, 2000);

        // Redirect to the cart page after 2 seconds
        setTimeout(() => {
            window.location.href = 'cart.html'; // Redirect to the cart page
        }, 2000);
    });
});

// Handle the "Add All to Cart" button click for checked products
var addTOcartButton = document.querySelector('.add-to-cart-button-catagoryPage');
if(addTOcartButton){
addTOcartButton.addEventListener('click', function () {
    var checkboxes = document.querySelectorAll('.product-checkbox');
    let anyChecked = false; // Flag to track if any checkboxes are selected

    // Iterate over each checkbox to check if it is selected
    checkboxes.forEach(checkbox => {
        if (checkbox.checked) {
            anyChecked = true;
            var productCard = checkbox.closest('.card-catagoryPage');
            var productImage = productCard.getAttribute('data-image');
            var productName = productCard.getAttribute('data-name');
            var productPrice = parseFloat(productCard.getAttribute('data-price'));
            var quantityInput = productCard.querySelector('.quantity-input');
            var quantity = parseInt(quantityInput.value);

            // Get today's date for start date
            var today = new Date();
            var startDate = today.toISOString().split('T')[0]; // Format as YYYY-MM-DD

            // Calculate the end date by adding the quantity (number of nights) to the start date
            var endDate = new Date(today);
            endDate.setDate(today.getDate() + quantity);  // Add the quantity to today's date
            var endDateString = endDate.toISOString().split('T')[0]; // Format end date as YYYY-MM-DD

            // Add product to the cart
            addToCart(productName, productImage, quantity, productPrice, startDate, endDateString);
        }
    });
	// Check if no products are checked
    if (!anyChecked) {
        // Display a message informing the user to select at least one product
        var alertMessage = document.getElementById('alert-message');
        alertMessage.style.display = 'block';

        // Hide the message after 3 seconds
        setTimeout(() => {
            alertMessage.style.display = 'none';
        }, 3000); // 3 seconds (3000 milliseconds)

        return; // Exit the function without adding anything to the cart
    }

    // Show confirmation message
    var confirmationMessage = document.getElementById('confirmation-message');
    confirmationMessage.style.display = 'block';

    // Hide the confirmation message after 2 seconds
    setTimeout(() => {
        confirmationMessage.style.display = 'none';
    }, 2000);

    // Redirect to the cart page after 2 seconds
    setTimeout(() => {
        window.location.href = 'cart.html'; // Redirect to the cart page
    }, 2000);
});

// Dropdown sorting logic for the category page
var dropdown = document.getElementById('sort');
var cardContainer = document.getElementById('category-cards');

dropdown.addEventListener('change', function() {
    var value = this.value;
    if (value === 'a-z') {
        sortCardsByName(true);
    } else if (value === 'z-a') {
        sortCardsByName(false);
    } else if (value === 'low-to-high') {
        sortCardsByPrice(true);
    } else if (value === 'high-to-low') {
        sortCardsByPrice(false);
    }
});
function sortCardsByName(ascending) {
    const cards = Array.from(cardContainer.getElementsByClassName('card-catagoryPage'));

    cards.sort((a, b) => {
        var nameA = a.getAttribute('data-name').toLowerCase();
        var nameB = b.getAttribute('data-name').toLowerCase();

        if (nameA < nameB) return ascending ? -1 : 1;
        if (nameA > nameB) return ascending ? 1 : -1;
        return 0;
    });

    updateCardContainer(cards);
}

function sortCardsByPrice(ascending) {
    var cards = Array.from(cardContainer.getElementsByClassName('card-catagoryPage'));

    cards.sort((a, b) => {
        var priceA = parseFloat(a.getAttribute('data-price'));
        var priceB = parseFloat(b.getAttribute('data-price'));

        return ascending ? priceA - priceB : priceB - priceA; // Ascending or Descending
    });

    updateCardContainer(cards);
}

function updateCardContainer(cards) {
    cardContainer.innerHTML = ''; // Clear the container
    cards.forEach(card => {
        cardContainer.appendChild(card);
    });
}

}
window.onload = function() {
    // Check if it's the first time the user visits the page
    if (!sessionStorage.getItem('visited')) {
        // Clear localStorage on first visit
        localStorage.clear();
        sessionStorage.setItem('visited', 'true');  // Mark as visited
    }

    // Display products
    displayProducts();
};


// Add product function :
function addProduct() {
    // Get the input from user
    var productName = document.getElementById('product-name').value;
    var productPhoto = document.getElementById('product-photo').files[0];
    var productPrice = document.getElementById('product-price').value;
    var productCategory = document.getElementById('product-category').value;
    var productQuantity = document.getElementById('product-quantity').value;
    var productDescription = document.getElementById('product-description').value;

    // Check if there is an empty field
    if (!productName  || !productPrice || !productCategory || !productQuantity || !productDescription || !productPhoto) {
        alert("All fields are required! Please fill all fields");
        return;
    }

     // Check if â€˜nameâ€™ field starts with number
    if (!isNaN(productName.charAt(0))) {
        alert("Product name cannot start with a number.");
        return;
    }

     // Quantities and prices should be numbers
    if (isNaN(productPrice) || productPrice <= 0) {
        alert("Product price must be a positive number.");
        return;
    }

    if (isNaN(productQuantity) || productQuantity <= 0) {
        alert("Product quantity must be a positive number.");
        return;
    }

    var reader = new FileReader();
    reader.onload = function(e) {
        var imageUrl = e.target.result; 

    // Create a product object     
       var newProduct = {
            name: productName,
            price: parseFloat(productPrice),
            category: productCategory,
            quantity: parseInt(productQuantity),
            description: productDescription,
            photo: imageUrl 
        };

    // Retrieve current products from local storage or initialize as an empty array :
        var products = JSON.parse(localStorage.getItem('products')) || [];
        products.push(newProduct);

         // Save updated products list to local storage
        localStorage.setItem('products', JSON.stringify(products));

         // Alert the user and clear the form
        alert("Product "+ productName+ " has been added successfully!");
        document.getElementById('productForm').reset();

         // Reload the dashboard to reflect the added product
        displayProducts();
        window.location.reload();
    };

    reader.readAsDataURL(productPhoto); 
   // localStorage.clear();
}


function displayProducts() {
    var container = document.getElementById('product-container'); 
    container.innerHTML = ''; 

    const products = JSON.parse(localStorage.getItem('products')) || [];

    if (products.length === 0) {
        container.innerHTML = "<p style='color: gray; text-align: center;'> There is no product to show! You have to add it first</p>";
        return;
    }

    products.forEach(product => {
        const productElement = document.createElement('div');
        productElement.classList.add('image-box-sellerdashboard');
        productElement.innerHTML = `
            <img src="${product.photo}" alt="${product.name}" class="promotion-image">
            <div class="text-content-sellerdashboard">
                <h3>${product.name}</h3>
                <p>: ${product.price} </p>
                <p>: ${product.quantity}</p>
                <p>${product.description}</p>
            </div>
        `;
        container.appendChild(productElement);

    });
    //localStorage.clear();
}

function checkboxes() {
    // Select all checkbox elements
    var checkboxes = document.querySelectorAll('input[type="checkbox"]');
    var deleteButton = document.querySelector('.button-EOP');

    // Delete button click event 
    deleteButton.addEventListener('click', () => {
        // Check if at least one checkbox is selected
        var selectedCheckboxes = Array.from(checkboxes).filter(checkbox => checkbox.checked);

        // If no checkboxes are selected, show an alert
        if (selectedCheckboxes.length === 0) {
            alert('Please select at least one offer');
            return;
        }

        // Ask for confirmation only once if at least one offer is selected
        var confirmDelete = confirm('Are you sure you want to delete the selected offers?');

        // If the user confirms, delete the selected offers
        if (confirmDelete) {
            selectedCheckboxes.forEach(checkbox => {
                // Get the closest offer element
                const offerElement = checkbox.closest('.The-Offer');
                
                // Extract the offer name
                const offerName = offerElement.querySelector('label').innerText.split(':')[0].trim();

                // Remove the offer from localStorage
                removeOfferFromLocalStorage(offerName);

                // Remove the offer from the DOM
                offerElement.remove();
            });

            alert('Selected offers have been deleted');
        }
    });
}

// Remove offer from localStorage based on name
function removeOfferFromLocalStorage(offerName) {
    // Load offers from localStorage
    var offers = loadOffersFromLocalStorage();
    
    // Filter out the offer to be deleted
    offers = offers.filter(offer => offer.name !== offerName);
    
    // Save the updated offers back to localStorage
    saveOffersToLocalStorage(offers);
}

// Load offers from localStorage
function loadOffersFromLocalStorage() {
    var offers = localStorage.getItem('offers');
    return offers ? JSON.parse(offers) : [];
}

// Save offers to localStorage
function saveOffersToLocalStorage(offers) {
    localStorage.setItem('offers', JSON.stringify(offers));
}

// Convert image to base64
function convertImageToBase64(file, callback) {
    var reader = new FileReader();
    reader.onloadend = function () {
        callback(reader.result);
    };
    reader.readAsDataURL(file); // Convert image to base64
}

// Add new offer and save to localStorage
function checkForms(event) {
    event.preventDefault(); // Prevent default form submission
    
    var Name = document.getElementById('offer-name').value;
    var Photo = document.getElementById('offer-photo').files[0];
    var Description = document.getElementById('offer-description').value;

    // Check if all required fields are filled
    if (!Name  || !Photo || !Description) {
        alert('Please fill in all fields before submitting');
        return;
    }

    // Check if â€˜nameâ€™ field starts with a number
    if (!isNaN(Name.charAt(0))) {
        alert("Offer name cannot start with a number!");
        return;
    }

    // Convert the image to base64
    convertImageToBase64(Photo, function (base64Image) {
        // Create the new offer object
        var newOffer = {
            name: Name,
            photo: base64Image, // Store the base64 string
            description: Description
        };

        // Save the new offer to localStorage
        var offers = loadOffersFromLocalStorage();
        offers.push(newOffer);
        saveOffersToLocalStorage(offers);
        // Create the new offer element
        var newOfferElement = document.createElement('div');
        newOfferElement.classList.add('The-Offer');
        
        // Add content to the new offer with the same structure and styling
        newOfferElement.innerHTML = `
            <div class="checkboxes"><input type="checkbox" value="offers"></div>
            <div class="offer-content">
                <div class="offer-image"><img src="${base64Image}" alt="${Name}"></div>
                <label><strong>${Name}:</strong><br>${Description}</label>
            </div>
        `;

        // Add the new offer to the offer list (at the end of the list)
        var offerList = document.querySelector('.modify-offers');
        offerList.appendChild(newOfferElement);

        // Show a confirmation message that the offer was added
        alert('New offer has been added successfully!');

        // Reset the form fields
        document.getElementById('productForm').reset();

        // Update checkboxes after adding a new offer
        checkboxes();
      localStorage.clear();

    });
}

// This function loads offers from localStorage
function loadOffersFromLocalStorage() {
    // Retrieve the 'offers' array from localStorage. If no offers are found, return an empty array.
    return JSON.parse(localStorage.getItem('offers')) || [];
}

function loadOffers() {
    // Get the offers using the loadOffersFromLocalStorage function
    var offers = loadOffersFromLocalStorage();

    // Check if there are any offers to display
    if (offers.length > 0) {
        // Loop through each offer and create an HTML element for it
        offers.forEach(offer => {
            var newOfferElement = document.createElement('div');
            newOfferElement.classList.add('The-Offer');
            newOfferElement.innerHTML = `
                <div class="checkboxes"><input type="checkbox" value="offers"></div>
                <div class="offer-content">
                    <div class="offer-image"><img src="${offer.photo}" alt="${offer.name}"></div>
                    <label><strong>${offer.name}:</strong><br>${offer.description}</label>
                </div>
            `;

            // Add the new offer to the offer list (at the end of the list)
            var offerList = document.querySelector('.modify-offers');
            offerList.appendChild(newOfferElement);
        });

        // Reinitialize the checkbox behavior after loading offers
        checkbox();
        localStorage.clear();
    }
}

// Call the loadOffers function when the page is loaded
document.addEventListener('DOMContentLoaded', loadOffers);


var productSelect = document.getElementById('product-select-productEval');
if(productSelect){
    document.querySelector('.submit-btn-productEval').addEventListener('click', function(event) {
        event.preventDefault(); // Prevent form submission
        

       var selectedProduct = productSelect.options[productSelect.selectedIndex].text;
        var productNumber = productSelect.value;
        
       var rating = document.querySelector('input[name="rating"]:checked');

        // Check if a product and a rating are selected
        if (!productNumber) {
            alert("Please select a product from your previous orders.");
            return;
        }
        if (!rating) {
            alert("Please select a rating.");
            return;
        }

        // Display alert message with product number and rating
      var userRating = rating.value;
        alert(`Thank you for your feedback! Your rating for product#${productNumber} is ${userRating}`);

      
        window.location.href = "index.html";
    });

}

