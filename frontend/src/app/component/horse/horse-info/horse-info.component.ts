import {Component} from '@angular/core';
import {Horse} from "../../../dto/horse";
import {Sex} from "../../../dto/sex";
import {HorseService} from "../../../service/horse.service";
import {BreedService} from "../../../service/breed.service";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgForm, NgModel} from "@angular/forms";
import {Breed} from "../../../dto/breed";
import {Observable, of} from "rxjs";
import {HorseCreateEditMode} from "../horse-create-edit/horse-create-edit.component";

@Component({
  selector: 'app-horse-info',
  templateUrl: './horse-info.component.html',
  styleUrls: ['./horse-info.component.scss']
})
export class HorseInfoComponent {

  horseId: number = 0;
  horse: Horse = {
    name: '',
    sex: Sex.female,
    dateOfBirth: new Date(), // TODO this is bad
    height: 0, // TODO this is bad
    weight: 0, // TODO this is bad
  };

  private heightSet: boolean = false;
  private weightSet: boolean = false;
  private dateOfBirthSet: boolean = false;

  get height(): number | null {
    return this.heightSet
      ? this.horse.height
      : null;
  }

  get weight(): number | null {
    return this.weightSet
      ? this.horse.weight
      : null;
  }

  get dateOfBirth(): Date | null {
    return this.dateOfBirthSet
      ? this.horse.dateOfBirth
      : null;
  }

  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    return 'Info';
  }

  get sex(): string {
    switch (this.horse.sex) {
      case Sex.male: return 'Male';
      case Sex.female: return 'Female';
      default: return '';
    }
  }

  ngOnInit(): void {
  this.route.paramMap.subscribe((params: ParamMap) => {

    const horseId = params.get('id');
    if(horseId !== null){
      const id = parseInt(horseId, 10);
      this.service.getById(id).subscribe((horse: Horse) => {
        this.horse = horse;
        this.heightSet = true;
        this.weightSet = true;
        this.dateOfBirthSet = true;

        console.log(horse)
      })
    }
  });
}


  public formatBreedName(breed: Breed | null): string {
    return breed?.name ?? '';
  }

}
