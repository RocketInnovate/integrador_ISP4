import { Component, AfterViewInit, ElementRef } from '@angular/core';
import * as bootstrap from 'bootstrap';

@Component({
  selector: 'app-about-us',
  templateUrl: './about-us.component.html',
  styleUrls: ['./about-us.component.css']
})
export class AboutUsComponent {

  constructor(private elementRef: ElementRef) {}

  ngAfterViewInit() {
    const testimonialCarousel = this.elementRef.nativeElement.querySelector("#testimonialCarousel");

    if (window.matchMedia("(min-width:576px)").matches) {
      const carousel = new bootstrap.Carousel(testimonialCarousel, { interval: false });

      let carouselWidth = testimonialCarousel.querySelector(".carousel-inner").scrollWidth;
      let cardWidth = testimonialCarousel.querySelector(".carousel-item").offsetWidth;

      let scrollPosition = 0;

      testimonialCarousel.querySelector(".carousel-control-next").addEventListener("click", () => {
        if (scrollPosition < carouselWidth - cardWidth * 3) {
          scrollPosition += cardWidth;
          testimonialCarousel.querySelector(".carousel-inner").scrollLeft += cardWidth;
        }
      });

      testimonialCarousel.querySelector(".carousel-control-prev").addEventListener("click", () => {
        if (scrollPosition > 0) {
          scrollPosition -= cardWidth;
          testimonialCarousel.querySelector(".carousel-inner").scrollLeft -= cardWidth;
        }
      });
    } else {
      testimonialCarousel.classList.add("slide");
    }
  }
}
