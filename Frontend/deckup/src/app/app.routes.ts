import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { VerifyComponent } from './components/verify/verify.component';
import { TiendaComponent } from './components/tienda/tienda.component';
import { PaymentsComponent } from './components/payments/payments.component';
import { ColeccionComponent } from './components/coleccion/coleccion.component';
import { ChatComponent } from './components/chat/chat.component';
import { DeckBuilderComponent } from './components/deck-builder/deck-builder.component';
import { MatchmakingComponent } from './components/matchmaking/matchmaking.component';
import { GameComponent } from './components/game/game.component';
import { CrudComponent } from './components/crud/crud.component';
import { AdminComponent } from './components/admin/admin.component';
import { TradeComponent } from './components/trade/trade.component';
import { TraderoomComponent } from './components/traderoom/traderoom.component';
import { ConfigComponent } from './components/config/config.component';
import { PrevGamesComponent } from './components/prev-games/prev-games.component';
import { PrevTradesComponent } from './components/prev-trades/prev-trades.component';
import { PerfilComponent } from './components/perfil/perfil.component';
import { DefaultComponent } from './components/default/default.component';
import { TutorialComponent } from './components/tutorial/tutorial.component';

export const routes: Routes = [
    {path: 'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'home', component: HomeComponent},
    {path: 'verify', component: VerifyComponent},
    {path: 'tienda', component: TiendaComponent},
    {path: 'tienda/verify', component: PaymentsComponent},
    {path: 'tienda/cancel', component: PaymentsComponent},
    {path: 'coleccion', component: ColeccionComponent},
    {path: 'chat/:username', component: ChatComponent},
    {path: 'deck-builder', component: DeckBuilderComponent},
    {path: 'matchmaking', component: MatchmakingComponent},
    {path: 'game', component: GameComponent},
    {path: 'admin', component: AdminComponent},
    {path: 'admin/crud/:module', component: CrudComponent},
    {path: 'trade', component: TradeComponent},
    {path: 'trade/:sala', component: TraderoomComponent},
    {path: 'config', component: ConfigComponent},
    {path: 'game/recientes', component: PrevGamesComponent},
    {path: 'trades/recientes', component: PrevTradesComponent},
    {path: 'perfil/:id', component: PerfilComponent},
    {path: '', component: DefaultComponent},
    {path: 'tutorial', component: TutorialComponent},
];
