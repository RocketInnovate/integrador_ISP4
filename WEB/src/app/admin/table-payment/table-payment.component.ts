import { Component, OnInit } from '@angular/core';
import { PaymentsService } from 'src/app/services/payments.service';

@Component({
  selector: 'app-table-payment',
  templateUrl: './table-payment.component.html',
  styleUrls: ['./table-payment.component.css']
})
export class TablePaymentComponent implements OnInit {


  constructor(private paymentsService: PaymentsService) { }

  paymentsData: { id: number, user: number, method: string, price: string, status: string }[] = [];

  ngOnInit() {
    this.paymentsService.getPayments()
      .subscribe(paymentsData => {
        this.paymentsData = paymentsData as { id: number; user: number; method: string; price: string; status: string; }[]; // Type assertion
      });
      console.log(this.paymentsData)
  }

}
