package com.example.tikectapp.ui.promo


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tikectapp.data.model.Promo

@Composable
fun PromoCarousel(
    promos: List<Promo>,
    onPromoClick: (Promo) -> Unit
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(promos) { promo ->

            Card(
                modifier = Modifier
                    .fillParentMaxWidth(0.9f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                onClick = {
                    onPromoClick(promo)
                }
            ) {

                Column {

                    Image(
                        painter = rememberAsyncImagePainter(
                            promo.bannerUrl
                        ),
                        contentDescription = promo.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 20.dp,
                                    topEnd = 20.dp
                                )
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = promo.title,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = promo.description
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Voucher: ${promo.voucherCode}"
                        )
                    }
                }
            }
        }
    }
}