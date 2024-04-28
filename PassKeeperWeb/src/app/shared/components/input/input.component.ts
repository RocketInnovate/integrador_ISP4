import { Component, Input } from '@angular/core';
import { FormControl, ValidationErrors } from '@angular/forms';

@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.css']
})
export class InputComponent {
  @Input() control!: FormControl;
  @Input() label!: string;
  @Input() controlName!: string;
  @Input() placeholder!: string;
  @Input() type: string = 'text';
  @Input() errorMessages!: { [key: string]: string };

  getFirstValidationError(errors: ValidationErrors | null): string {
    if (!errors) {
      return '';
    }
    const firstErrorKey = Object.keys(errors)[0];
    return firstErrorKey;
  }
}
