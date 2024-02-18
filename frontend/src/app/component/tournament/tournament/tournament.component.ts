import { Component, OnInit } from '@angular/core';
import {TournamentListDto, TournamentSearchParams} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ToastrService} from "ngx-toastr";
import {debounceTime, map, Observable, of, Subject} from 'rxjs';

@Component({
  selector: 'app-tournament',
  templateUrl: './tournament.component.html',
  styleUrls: ['./tournament.component.scss']
})
export class TournamentComponent implements OnInit{
  tournaments: TournamentListDto[] = [];
  searchParams: TournamentSearchParams = {};
  bannerError: string | null = null;
  searchStartDate: string | null = null;
  searchEndDate: string | null = null;
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: TournamentService,
    private notification: ToastrService,
  ) {
  }
  ngOnInit(): void {
    this.reloadTournaments();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadTournaments()});
  }
  reloadTournaments() {
    if (this.searchStartDate) {
      this.searchParams.startDate = new Date(this.searchStartDate);
    } else {
      this.searchParams.startDate = undefined;
    }
    if (this.searchEndDate) {
      this.searchParams.endDate = new Date(this.searchEndDate);
    } else {
      this.searchParams.endDate = undefined;
    }

    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.tournaments = data;
        },
        error: error => {
          console.error('Error fetching tournaments', error);
          this.bannerError = 'Could not fetch tournaments: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Tournaments');
        }
      });
  }

  searchChanged(): void {
    this.searchChangedObservable.next();
  }

  convertDate(date: Date): string {
    return new Date(date).toLocaleDateString();
  }
}
