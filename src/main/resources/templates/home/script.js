let slideIndex = 0;

function moveCarousel(direction) {
    const carousel = document.querySelector('.carousel');
    const cards = document.querySelectorAll('.card');
    const cardWidth = cards[0].offsetWidth;
    const maxIndex = cards.length - 1;

    slideIndex += direction;

    if (slideIndex > maxIndex) {
        slideIndex = 0;
    } else if (slideIndex < 0) {
        slideIndex = maxIndex;
    }

    carousel.style.transform = `translateX(-${slideIndex * cardWidth}px)`;
}