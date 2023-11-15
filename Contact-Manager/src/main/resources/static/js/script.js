console.log("This is Java script");

const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};


const search = () => {
    let query = $("#search-input").val();

    if (query == "") {
        $(".search-result").hide();
    } else {
        let url = `http://localhost:8091/search/${query}`;

        fetch(url)
            .then((response) => response.json())
            .then((data) => {
                let text = `<div class='list-group'>`;

                data.forEach((contact) => {
                    text += `<a href='/contact/${contact.cid}' class='list-group-item list-group-action'> ${contact.cname} </a>`;
                });

                text += `</div>`;

                $(".search-result").html(text);
                $(".search-result").show();
            })
            .catch((error) => {
                console.error("Error fetching data:", error);
            });
    }
};

// First Request to server to create order
const paymentStart = () => {
    console.log("Payment Started...");
    let amount = $("#payment_field").val();
    console.log(amount);
    if (amount === "" || amount === null) {
        //alert("Amount is required!!");
        swal("Failed!", "Amount is required!!", "error");

        return;
    }

    // Send request to create order using Ajax
    $.ajax({
        url: '/createorder',
        data: JSON.stringify({ amount: amount, info: 'order_request' }),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success: function(response) {
            // Invoked when the request is successful
            console.log(response);
            if (response.status === 'created') {
                // Open payment form
                let options = {
                    key: 'rzp_test_ss2ub5A5qePEy6',
                    amount: response.amount,
                    currency: 'INR',
                    name: 'Smart Contact Manager',
                    description: 'Donation',
                    image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTSYXcFIkSQdmP_yjM7BTyEfZwcw-uiw4SF2w&usqp=CAU',
                    order_id: response.id,
                    handler: function(response) {
                        console.log(response.razorpay_payment_id);
                        console.log(response.razorpay_order_id);
                        console.log(response.razorpay_signature);
                        console.log("Payment successful!!");
                        //alert("Congratulations!! Payment Successful");
                        swal("Good job!", "Congratulations!! Payment Successfull", "success");


                    },
                    prefill: {
                        name: '',
                        email: '',
                        contact: ''
                    },
                    notes: {
                        address: 'Mohammed Farhan Ahmed'
                    },
                    theme: {
                        color: '#3399cc'
                    }
                };

                let rzp = new Razorpay(options);
                rzp.on('payment.failed', function(response) {
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    //alert('Oops! Payment Failed');
                    swal("Failed!", "Oops! Payment Failed!", "error");

                    
                });

                rzp.open();
            }
        },
        error: function(error) {
            // Invoked when there's an error
            console.log(error);
            alert('Something went wrong!!');
        }
    });
};
