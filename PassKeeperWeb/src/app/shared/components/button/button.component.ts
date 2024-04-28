import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.css']
})
export class ButtonComponent {
  @Input() text: string = '';
  @Input() type: string = 'button'; // 'submit', 'button', 'reset'
  @Input() btnClass: string = 'btn-primary'; // Bootstrap classes like 'btn-secondary', 'btn-success', etc.
  @Output() onClick: EventEmitter<void> = new EventEmitter<void>();
}
